// TransportationController.java (單一 List 取代兩列結構)
package com.islevilla.patty.transportation;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class TransportationController {

    private final WeatherApiService weatherService;

    public TransportationController(WeatherApiService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/transportation")
    public String transportationPage(@RequestParam(defaultValue = "Taipei") String city, Model model) {
        WeatherApiResponse weather = weatherService.get7DayForecast(city);
        if (weather != null && weather.getForecast() != null) {
            model.addAttribute("forecastList", weather.getForecast().getForecastday());
        }
        return "front-end/customer/transportation";
    }

    @Service
    public static class WeatherApiService {

        @Value("${weatherapi.key}")
        private String apiKey;

        private final RestTemplate restTemplate = new RestTemplate();

        public WeatherApiResponse get7DayForecast(String city) {
            String url = String.format(
                "https://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=7&lang=zh_tw",
                apiKey, city
            );
            return restTemplate.getForObject(url, WeatherApiResponse.class);
        }
    }

    @Data
    public static class WeatherApiResponse {
        private Forecast forecast;
    }

    @Data
    public static class Forecast {
        private List<ForecastDay> forecastday;
    }

    @Data
    public static class ForecastDay {
        private String date;
        private Day day;
    }

    @Data
    public static class Day {
        private double maxtemp_c;
        private double mintemp_c;
        private Condition condition;
    }

    @Data
    public static class Condition {
        private String text;
        private String icon;
    }
}
