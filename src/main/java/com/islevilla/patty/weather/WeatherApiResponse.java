package com.islevilla.patty.weather;

import java.util.List;

import lombok.Data;

@Data
public class WeatherApiResponse {
	
		public Location location;
	    public Forecast forecast;

	    @Data
	    public static class Location {
	        public String name;
	    }

	    @Data
	    public static class Forecast {
	        public List<ForecastDay> forecastday;
	    }

	    @Data
	    public static class ForecastDay {
	        public String date;
	        public Day day;
	    }

	    @Data
	    public static class Day {
	        public double maxtemp_c;
	        public double mintemp_c;
	        public Condition condition;
	    }

	    @Data
	    public static class Condition {
	        public String text;
	        public String icon;
	    }
	}
