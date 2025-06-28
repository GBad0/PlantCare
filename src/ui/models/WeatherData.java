package ui.models;

import java.util.List;
import java.util.ArrayList;

public class WeatherData {
    private String cityName;
    private double temperature;
    private String description;
    private int humidity;
    private double windSpeed;
    private String icon;
    private long timestamp;
    private List<DailyForecast> dailyForecasts;

    public WeatherData(String cityName, double temperature, String description, 
                      int humidity, double windSpeed, String icon) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.icon = icon;
        this.timestamp = System.currentTimeMillis();
        this.dailyForecasts = new ArrayList<>();
    }

    // Getters
    public String getCityName() { return cityName; }
    public double getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getIcon() { return icon; }
    public long getTimestamp() { return timestamp; }
    public List<DailyForecast> getDailyForecasts() { return dailyForecasts; }

    // Setters
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setDescription(String description) { this.description = description; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDailyForecasts(List<DailyForecast> dailyForecasts) { this.dailyForecasts = dailyForecasts; }

    @Override
    public String toString() {
        return String.format("WeatherData{city='%s', temp=%.1f°C, desc='%s', humidity=%d%%, wind=%.1f m/s, forecasts=%d}", 
                           cityName, temperature, description, humidity, windSpeed, dailyForecasts.size());
    }

    // Classe interna para previsão diária
    public static class DailyForecast {
        private String date;
        private double maxTemp;
        private double minTemp;
        private String description;
        private String icon;
        private int humidity;
        private double windSpeed;

        public DailyForecast(String date, double maxTemp, double minTemp, String description, 
                           String icon, int humidity, double windSpeed) {
            this.date = date;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.description = description;
            this.icon = icon;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }

        // Getters
        public String getDate() { return date; }
        public double getMaxTemp() { return maxTemp; }
        public double getMinTemp() { return minTemp; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }

        // Setters
        public void setDate(String date) { this.date = date; }
        public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }
        public void setMinTemp(double minTemp) { this.minTemp = minTemp; }
        public void setDescription(String description) { this.description = description; }
        public void setIcon(String icon) { this.icon = icon; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    }
} 