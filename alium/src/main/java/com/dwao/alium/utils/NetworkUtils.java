package com.dwao.alium.utils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static String makeGetRequest(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the URL object
            URL url = new URL(urlString);

            // Open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000); // 10 seconds
            urlConnection.setReadTimeout(10000);    // 10 seconds

            // Connect to the server
            urlConnection.connect();

            // Read the response
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            reader = new BufferedReader(inputStreamReader);
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ignored) {}
        }
    }
    public static String makePostRequest(String urlString, String jsonInputString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            StringBuilder response = getResponse(jsonInputString, conn);

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    @NonNull
    private static StringBuilder getResponse(String jsonInputString, HttpURLConnection conn) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;

        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response;
    }

}

