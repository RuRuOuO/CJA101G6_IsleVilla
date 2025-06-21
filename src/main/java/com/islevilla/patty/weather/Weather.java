package com.islevilla.patty.weather;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.io.IOException;

public class Weather {

    public static void main(String[] args) throws IOException, InterruptedException {

        String apiUrl = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/O-A0001-001?Authorization=CWA-97C55E07-4A4C-4F2E-81DA-6CBC30CAC58E&StationId=C0W150";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());
            
            // 取得 records → location 資料陣列
            JSONArray locations = json.getJSONObject("records").getJSONArray("location");

            for (int i = 0; i < locations.length(); i++) {
                JSONObject location = locations.getJSONObject(i);
                String locationName = location.getString("locationName");
                String stationId = location.getString("stationId");

                System.out.println("站點名稱: " + locationName);
                System.out.println("測站代碼: " + stationId);
            }

        } else {
            System.out.println("請求失敗: " + response.statusCode());
        }
    }
}
