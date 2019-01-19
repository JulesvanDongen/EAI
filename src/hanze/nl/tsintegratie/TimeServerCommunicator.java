package hanze.nl.tsintegratie;



import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TimeServerCommunicator {

    public static final String TIMESERVER_URL = "http://localhost:5002/TimeServer";

    public TimeServerCommunicator() {
    }

    public String getCurrentTime() throws IOException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();

        HttpGet request = new HttpGet(TIMESERVER_URL);
        CloseableHttpResponse response = httpClient.execute(request);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        return bufferedReader.readLine();
    }
}
