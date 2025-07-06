package com.islevilla.patty.weather;

import java.util.List;

public class WeatherApiResponse {
	 public Location location;
	    public Forecast forecast;

	    public static class Location {
	        public String name;
	    }

	    public static class Forecast {
	        public List<ForecastDay> forecastday;
	    }

	    public static class ForecastDay {
	        public String date;
	        public Day day;
	    }

	    public static class Day {
	        public double maxtemp_c;
	        public double mintemp_c;
	        public Condition condition;
	    }

	    public static class Condition {
	        public String text;
	        public String icon;
	    }
	}
