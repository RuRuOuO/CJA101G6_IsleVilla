package com.islevilla.patty.transportation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.patty.weather.WeatherApiResponse;
import com.islevilla.patty.weather.WeatherApiService;



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
}
