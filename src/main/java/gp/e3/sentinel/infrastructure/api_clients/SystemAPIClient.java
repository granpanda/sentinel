package gp.e3.sentinel.infrastructure.api_clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import gp.e3.sentinel.Sentinel;
import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.infrastructure.utils.HttpUtils;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SystemAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemAPIClient.class);

    private final ObjectMapper objectMapper;
    private final HttpClientBuilder httpClientBuilder;

    private SystemAPIClient(ObjectMapper objectMapper, HttpClientBuilder httpClientBuilder) {
        this.objectMapper = objectMapper;
        this.httpClientBuilder = httpClientBuilder;
    }

    public static SystemAPIClient getSystemAPIClient() {

        ObjectMapper objectMapper = Sentinel.configureJackson(Jackson.newObjectMapper());
        RequestConfig defaultRequestConfig = HttpUtils.getDefaultRequestConfig();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig);

        return new SystemAPIClient(objectMapper, httpClientBuilder);
    }

    public Request checkSystemIsAlive(int workerId, gp.e3.sentinel.domain.entities.System system) {

        Request request = null;

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        String url = system.getUrl();
        int statusCode = 0;
        String responseEntity = "";
        long initialTime = DateTime.now().getMillis();

        try {

            HttpGet getRequest = new HttpGet(url);
            httpClient = httpClientBuilder.build();
            httpResponse = httpClient.execute(getRequest);

            LOGGER.info("checkSystemIsAlive: " + getRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            LOGGER.info("checkSystemIsAlive: " + statusCode);
            responseEntity = HttpUtils.getHttpEntityAsString(httpResponse.getEntity());

        } catch (IOException e) {

            LOGGER.error("checkSystemIsAlive: " + e);
            responseEntity = e.getMessage();

        } finally {

            IOUtils.closeQuietly(httpClient);
            IOUtils.closeQuietly(httpResponse);

            long finalTime = DateTime.now().getMillis();
            long executionTime = finalTime - initialTime;
            request = new Request(workerId, system.getId(), system.getName(), url, statusCode, responseEntity, DateTime.now(), executionTime);
        }

        return request;
    }
}
