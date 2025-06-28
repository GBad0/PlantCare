package ui.services;

import ui.models.WeatherData;
import ui.models.WeatherData.DailyForecast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class WeatherService {
    // ⚠️ IMPORTANTE: Substitua pela sua chave válida da OpenWeatherMap
    // Obtenha em: https://openweathermap.org/api
    private static final String API_KEY = System.getenv("WEATHER_API_KEY");
    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast";
    
    public WeatherData getWeatherData(String cityName) throws IOException, JSONException {
        // Buscar dados atuais
        WeatherData currentWeather = getCurrentWeather(cityName);
        
        // Buscar previsão de 5 dias
        List<DailyForecast> forecasts = getForecastData(cityName);
        currentWeather.setDailyForecasts(forecasts);
        
        return currentWeather;
    }
    
    private WeatherData getCurrentWeather(String cityName) throws IOException, JSONException {
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String urlString = String.format("%s?q=%s&appid=%s&units=metric&lang=pt_br", 
                                       CURRENT_WEATHER_URL, encodedCity, API_KEY);
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Erro na API: " + responseCode + " - " + connection.getResponseMessage());
        }
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
        );
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        
        return parseCurrentWeatherData(response.toString());
    }
    
    private List<DailyForecast> getForecastData(String cityName) throws IOException, JSONException {
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String urlString = String.format("%s?q=%s&appid=%s&units=metric&lang=pt_br", 
                                       FORECAST_URL, encodedCity, API_KEY);
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Erro na API de previsão: " + responseCode + " - " + connection.getResponseMessage());
        }
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
        );
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        
        return parseForecastData(response.toString());
    }
    
    private WeatherData parseCurrentWeatherData(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);
        
        String cityName = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        double temperature = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        String icon = weather.getString("icon");
        
        JSONObject wind = json.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");
        
        return new WeatherData(cityName, temperature, description, humidity, windSpeed, icon);
    }
    
    private List<DailyForecast> parseForecastData(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);
        JSONArray list = json.getJSONArray("list");
        
        List<DailyForecast> forecasts = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Agrupar dados por dia (próximos 3 dias)
        for (int i = 1; i <= 3; i++) {
            LocalDate forecastDate = currentDate.plusDays(i);
            String dateStr = forecastDate.format(formatter);
            
            // Encontrar dados para este dia (usar dados do meio-dia se disponível)
            double maxTemp = -100, minTemp = 100;
            String description = "";
            String icon = "";
            int humidity = 0;
            double windSpeed = 0;
            int count = 0;
            
            for (int j = 0; j < list.length(); j++) {
                JSONObject item = list.getJSONObject(j);
                String dtTxt = item.getString("dt_txt");
                
                // Verificar se é o dia correto
                if (dtTxt.startsWith(forecastDate.toString())) {
                    JSONObject main = item.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    maxTemp = Math.max(maxTemp, temp);
                    minTemp = Math.min(minTemp, temp);
                    
                    JSONObject weather = item.getJSONArray("weather").getJSONObject(0);
                    description = weather.getString("description");
                    icon = weather.getString("icon");
                    
                    humidity = main.getInt("humidity");
                    windSpeed = item.getJSONObject("wind").getDouble("speed");
                    count++;
                }
            }
            
            if (count > 0) {
                forecasts.add(new DailyForecast(dateStr, maxTemp, minTemp, description, icon, humidity, windSpeed));
            }
        }
        
        return forecasts;
    }
    
    public String getWeatherIconUrl(String iconCode) {
        return String.format("http://openweathermap.org/img/wn/%s@2x.png", iconCode);
    }
} 