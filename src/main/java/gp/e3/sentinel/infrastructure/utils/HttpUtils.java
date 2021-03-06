package gp.e3.sentinel.infrastructure.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

public class HttpUtils {

	public static final int CONNECT_TIMEOUT_IN_MILLIS = 20000;

	public static String getHttpEntityAsString(HttpEntity httpEntity) throws IOException {

		String answer = "";

		if (httpEntity != null) {

			InputStream contentInputStream = httpEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(contentInputStream));

			String line = br.readLine();

			while (line != null && !line.isEmpty()) {

				answer += line;
				line = br.readLine();
			}
		}

		return answer;
	}

	public static RequestConfig getDefaultRequestConfig() {

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(CONNECT_TIMEOUT_IN_MILLIS)
				.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLIS)
				.setConnectionRequestTimeout(CONNECT_TIMEOUT_IN_MILLIS)
				.build();

		return requestConfig;
	}

    public static boolean isSuccessfulRequest(int statusCode) {

        return (statusCode >= 200) && (statusCode < 300);
    }
}