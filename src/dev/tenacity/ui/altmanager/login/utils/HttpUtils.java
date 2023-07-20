package dev.tenacity.ui.altmanager.login.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tenacity.ui.altmanager.login.http.apache.ApacheHttpEngine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
    private static final ApacheHttpEngine engine = new ApacheHttpEngine();

    public static Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    public static String post(String url, String body) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        con.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        return con.getResponseMessage();
    }

    public static String buildParam(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            stringBuilder
                    .append(stringStringEntry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(stringStringEntry.getValue(), "utf-8"))
                    .append("&");
        }
        return stringBuilder.toString();
    }

    public static String buildUrl(String url, Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (!map.isEmpty()) {
            stringBuilder.append("?");
            stringBuilder.append(buildParam(map));
        }
        return stringBuilder.toString();
    }

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static ApacheHttpEngine getEngine() {
        return engine;
    }
}
