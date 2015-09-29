package gp.e3.sentinel.domain.workers;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.domain.repositories.UserRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.HttpUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CheckSingleSystemWorker implements Runnable {

    public static final String MQ_HOST = CheckAllSystemsJob.MQ_HOST;
    public static final String QUEUE_NAME = CheckAllSystemsJob.QUEUE_NAME;
    public static String NEW_LINE = java.lang.System.getProperty("line.separator");

    public static final String HOST_NAME = "smtp.googlemail.com";
    public static final int SMTP_PORT = 465;
    public static final String SENDER_USERNAME = "e3.granpanda@gmail.com";
    public static final String SENDER_PASSWORD = "bsip5102";
    public static final String SENDER_EMAIL = "e3.granpanda@gmail.com";

    private final Gson gson;

    private Connection rabbitConnection;
    private Channel rabbitChannel;

    private java.sql.Connection dbConnection;
    private final BasicDataSource dataSource;

    private Jedis redisClient;
    private JedisPool redisPool;

    private final UserRepository userRepository;
    private final SystemRepository systemRepository;
    private final RequestRepository requestRepository;
    private final HttpClientBuilder httpClientBuilder;

    public CheckSingleSystemWorker(Gson gson, Connection rabbitConnection, BasicDataSource dataSource, JedisPool redisPool, UserRepository userRepository,
                                   SystemRepository systemRepository, RequestRepository requestRepository, HttpClientBuilder httpClientBuilder) {

        this.gson = gson;
        this.rabbitConnection = rabbitConnection;

        try {

            int prefetchCount = 1;
            this.rabbitChannel = RabbitHandler.getRabbitChannelAndInitializeQueue(rabbitConnection, CheckAllSystemsJob.QUEUE_NAME);
            rabbitChannel.basicQos(prefetchCount);

        } catch (IOException e) {

            e.printStackTrace();
        }

        this.dataSource = dataSource;
        this.redisPool = redisPool;

        this.userRepository = userRepository;
        this.systemRepository = systemRepository;
        this.requestRepository = requestRepository;

        this.httpClientBuilder = httpClientBuilder;
        RequestConfig defaultRequestConfig = HttpUtils.getDefaultRequestConfig();
        this.httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
    }

    private boolean initializeRabbitConnectionAndChannelIfNeeded() {

        boolean connectionAndChannelAreOpen = false;

        try {

            if (rabbitConnection == null || !rabbitConnection.isOpen()) {

                RabbitHandler.closeChannel(rabbitChannel);
                rabbitConnection = RabbitHandler.getRabbitConnection(MQ_HOST);
            }

            if (rabbitChannel == null || !rabbitChannel.isOpen()) {

                int prefetchCount = 1;
                rabbitChannel = RabbitHandler.getRabbitChannelAndInitializeQueue(rabbitConnection, QUEUE_NAME);
                rabbitChannel.basicQos(prefetchCount);
            }

            connectionAndChannelAreOpen = rabbitConnection.isOpen() && rabbitChannel.isOpen();

        } catch (IOException e) {

            e.printStackTrace();
            RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);
            connectionAndChannelAreOpen = false;
        }

        return connectionAndChannelAreOpen;
    }

    private boolean initializeDbConnection() {

        boolean dbConnectionIsOpen = false;

        try {

            if (dbConnection == null || dbConnection.isClosed()) {

                dbConnection = dataSource.getConnection();
            }

            dbConnectionIsOpen = !dbConnection.isClosed();

        } catch (SQLException e) {

            e.printStackTrace();
            dbConnectionIsOpen = false;
        }

        return dbConnectionIsOpen;
    }

    private boolean initializeRedisClient() {

        redisClient = redisPool.getResource();
        return redisClient.isConnected();
    }

    private boolean initializeMqAndDatabaseArtifactsIfNeeded() {

        boolean connectionAndChannelAreOpen = initializeRabbitConnectionAndChannelIfNeeded();
        boolean dbConnectionIsOpen = initializeDbConnection();
        boolean redisClientIsConnected = initializeRedisClient();

        return connectionAndChannelAreOpen && dbConnectionIsOpen && redisClientIsConnected;
    }

    private QueueingConsumer initializeQueueConsumerIfNeeded(QueueingConsumer consumer) throws IOException {

        if (consumer == null) {

            consumer = new QueueingConsumer(rabbitChannel);
            boolean autoAck = false;
            rabbitChannel.basicConsume(QUEUE_NAME, autoAck, consumer);
        }

        return consumer;
    }

    private Request checkSystemHealthAndReturnRequest(System system) {

        Request request = null;

        long requestId = 0;
        long systemId = system.getId();
        String systemName = system.getName();
        String systemUrl = system.getUrl();

        long initialTime = java.lang.System.currentTimeMillis();

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {

            httpClient = httpClientBuilder.build();
            HttpGet getRequest = new HttpGet(systemUrl);

            String applicationJson = ContentType.APPLICATION_JSON.toString();
            getRequest.addHeader("Accept", applicationJson);
            getRequest.addHeader("Content-Type", applicationJson + "; charset=UTF-8");

            httpResponse = httpClient.execute(getRequest);
            long finalTime = java.lang.System.currentTimeMillis();

            int httpResponseStatusCode = httpResponse.getStatusLine().getStatusCode();
            String httpResponseEntity = HttpUtils.getHttpEntityAsString(httpResponse.getEntity());

            DateTime requestExecutionDate = DateTime.now();
            long requestExecutionTimeInMilliseconds = finalTime - initialTime;

            request = new Request(requestId, systemId, systemName, systemUrl, httpResponseStatusCode, httpResponseEntity,
                    requestExecutionDate, requestExecutionTimeInMilliseconds);

        } catch (IOException e) {

            long finalTime = java.lang.System.currentTimeMillis();

            e.printStackTrace();

            int httpResponseStatusCode = 500;
            String httpResponseEntity = e.getMessage();

            DateTime requestExecutionDate = DateTime.now();
            long requestExecutionTimeInMilliseconds = finalTime - initialTime;

            request = new Request(requestId, systemId, systemName, systemUrl, httpResponseStatusCode, httpResponseEntity,
                    requestExecutionDate, requestExecutionTimeInMilliseconds);

        } finally {

            HttpUtils.closeHttpClientAndHttpResponse(httpClient, httpResponse);
        }

        return request;
    }

    private boolean requestWasSuccessful(Request request) {

        int statusCode = request.getHttpResponseStatusCode();
        return (statusCode >= 200 && statusCode < 300);
    }

    private String getMailBody(long requestId, Request request) {

        String systemInfo = "ID: " + request.getSystemId() + NEW_LINE +
                "Name: " + request.getSystemName() + NEW_LINE +
                "URL: " + request.getSystemUrl();

        String requestInfo = "ID: " + requestId + NEW_LINE +
                "Http status code: " + request.getHttpResponseStatusCode() + NEW_LINE +
                "Http entity: " + request.getHttpResponseEntity() + NEW_LINE +
                "execution time: " + request.getRequestExecutionTimeInMilliseconds() + " milliseconds.";

        String thanks = "Thanks, " + NEW_LINE + NEW_LINE + "The E3 engineering team." + NEW_LINE;

        String mailBody = NEW_LINE + "The system: " + NEW_LINE + NEW_LINE + systemInfo + NEW_LINE + NEW_LINE + NEW_LINE +
                "Failed on the following request: " + NEW_LINE + NEW_LINE + requestInfo + NEW_LINE + NEW_LINE +
                thanks + NEW_LINE;

        java.lang.System.out.println("mail body:");
        java.lang.System.out.println(mailBody);

        return mailBody;
    }

    private void notifyRequestByEmail(long requestId, Request request, List<User> systemUsers) {

        try {

            Email mail = new SimpleEmail();
            mail.setHostName(HOST_NAME);
            mail.setSmtpPort(SMTP_PORT);

            String senderUsername = SENDER_USERNAME;
            String senderPassword = SENDER_PASSWORD;
            mail.setAuthenticator(new DefaultAuthenticator(senderUsername, senderPassword));
            mail.setSSLOnConnect(true);
            mail.setFrom(SENDER_EMAIL);

            String systemName = request.getSystemName();
            DateTime requestExecutionDate = request.getRequestExecutionDate();
            mail.setSubject("E3 Warning: " + systemName + " at " + requestExecutionDate.toString());
            mail.setMsg(getMailBody(requestId, request));

            for (User user : systemUsers) {

                mail.addTo(user.getMail());
            }

            mail.send();

        } catch (Exception e) {

            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void run() {

        QueueingConsumer consumer = null;

        while (true) {

            try {

                boolean artifactsWereInitialized = initializeMqAndDatabaseArtifactsIfNeeded();
                consumer = initializeQueueConsumerIfNeeded(consumer);

                if (artifactsWereInitialized && consumer != null) {

                    Delivery delivery = consumer.nextDelivery();
                    String systemAsJsonString = new String(delivery.getBody());

                    System system = gson.fromJson(systemAsJsonString, System.class);
                    long systemId = system.getId();

                    Request request = checkSystemHealthAndReturnRequest(system);
                    long requestId = requestRepository.createRequest(dbConnection, request);

                    if (request != null && requestId != 0) {

                        if (!requestWasSuccessful(request)) {

                            if (!systemRepository.isSystemInCache(redisClient, systemId)) {

                                List<User> systemUsers = userRepository.getAllUsersSubscribedToASystem(dbConnection, systemId);
                                notifyRequestByEmail(requestId, request, systemUsers);
                                systemRepository.addSystemToCacheWithTimeToLive(redisClient, system);
                            }

                        } else {

                            systemRepository.deleteSystemFromCache(redisClient, systemId);
                        }

                        RabbitHandler.acknowledgeMessage(rabbitChannel, delivery);
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

                consumer = null;
                RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);

            } finally {

                SqlUtils.closeDbConnection(dbConnection);
                redisClient.close();
            }
        }
    }
}