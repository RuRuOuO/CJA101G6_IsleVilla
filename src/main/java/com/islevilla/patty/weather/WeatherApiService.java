package com.islevilla.patty.weather;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherApiService {

    @Value("${weatherapi.key}")
    private String apiKey;

    @Value("${weatherapi.url}")
    private String apiUrl;

    private static final Map<String, String> zhToZhTwMap = Map.ofEntries(
        Map.entry("晴", "晴"),
        Map.entry("多云", "多雲"),
        Map.entry("阴", "陰"),
        Map.entry("雷阵雨", "雷陣雨"),
        Map.entry("小雨", "小雨"),
        Map.entry("中雨", "中雨"),
        Map.entry("大雨", "大雨"),
        Map.entry("阵雨", "陣雨"),
        Map.entry("雾", "霧"),
        Map.entry("沙尘暴", "沙塵暴"),
        Map.entry("边", "邊"),
        Map.entry("周边", "周邊")
    );

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherApiResponse get7DayForecast(String city) {
        String url = String.format("%s?key=%s&q=%s&days=7&lang=zh", apiUrl, apiKey, city);
        WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

        if (response != null && response.forecast != null) {
            for (var day : response.forecast.forecastday) {
                String simplified = day.day.condition.text;
                String traditional = convertToTraditional(simplified);
                day.day.condition.text = traditional;
            }
        }

        return response;
    }

    private String convertToTraditional(String simplified) {
        String traditional = simplified;
        for (Map.Entry<String, String> entry : zhToZhTwMap.entrySet()) {
            traditional = traditional.replace(entry.getKey(), entry.getValue());
        }
        return traditional;
    }
}
