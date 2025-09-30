package com.ticketing;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class HealthCheck
{

    static void pingDatabase() throws Exception
    {
        String healthCheckUrl = "http://localhost:8080/health"; //To be replaced with a config read. Either JSON or yml

        URI uri = new URI(healthCheckUrl);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Application is healthy.");
        }
    }
}