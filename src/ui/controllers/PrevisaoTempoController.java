package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import ui.models.WeatherData;
import ui.models.WeatherData.DailyForecast;
import ui.services.WeatherService;
import java.io.IOException;
import org.json.JSONException;

public class PrevisaoTempoController {
    @FXML private TextField cityInput;
    @FXML private Button searchButton;
    @FXML private VBox weatherContainer;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private VBox loadingContainer;
    
    private WeatherService weatherService;
    
    @FXML
    private void initialize() {
        weatherService = new WeatherService();
        weatherContainer.setVisible(false);
        loadingContainer.setVisible(false);
        statusLabel.setText("Digite o nome de uma cidade para ver a previsão do tempo");
        
        // Configurar o container para permitir crescimento
        weatherContainer.setPrefWidth(Double.MAX_VALUE);
        weatherContainer.setMaxWidth(Double.MAX_VALUE);
    }
    
    @FXML
    private void searchWeather() {
        String cityName = cityInput.getText().trim();
        if (cityName.isEmpty()) {
            showAlert("Erro", "Por favor, digite o nome de uma cidade");
            return;
        }
        
        // Mostrar loading
        loadingContainer.setVisible(true);
        weatherContainer.setVisible(false);
        statusLabel.setText("Buscando dados do tempo...");
        searchButton.setDisable(true);
        
        // Executar busca em thread separada
        Task<WeatherData> task = new Task<WeatherData>() {
            @Override
            protected WeatherData call() throws Exception {
                return weatherService.getWeatherData(cityName);
            }
        };
        
        task.setOnSucceeded(event -> {
            WeatherData weatherData = task.getValue();
            Platform.runLater(() -> displayWeatherData(weatherData));
        });
        
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            Platform.runLater(() -> {
                loadingContainer.setVisible(false);
                searchButton.setDisable(false);
                if (exception instanceof IOException) {
                    statusLabel.setText("Erro de conexão. Verifique sua internet.");
                } else if (exception instanceof JSONException) {
                    statusLabel.setText("Erro ao processar dados da API.");
                } else {
                    statusLabel.setText("Cidade não encontrada ou erro na API.");
                }
                showAlert("Erro", "Não foi possível obter dados do tempo: " + exception.getMessage());
            });
        });
        
        new Thread(task).start();
    }
    
    private void displayWeatherData(WeatherData weatherData) {
        loadingContainer.setVisible(false);
        searchButton.setDisable(false);
        weatherContainer.setVisible(true);
        statusLabel.setText("Dados atualizados em " + 
                          java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        
        // Limpar container anterior
        weatherContainer.getChildren().clear();
        
        // Criar layout da previsão atual
        VBox currentWeatherBox = createCurrentWeatherBox(weatherData);
        weatherContainer.getChildren().add(currentWeatherBox);
        
        // Criar layout das previsões dos próximos 3 dias
        if (!weatherData.getDailyForecasts().isEmpty()) {
            VBox forecastBox = createForecastBox(weatherData);
            weatherContainer.getChildren().add(forecastBox);
        }
        
        // Forçar atualização do layout
        weatherContainer.requestLayout();
    }
    
    private VBox createCurrentWeatherBox(WeatherData weatherData) {
        VBox weatherBox = new VBox(15);
        weatherBox.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #4682B4); " +
                           "-fx-background-radius: 10; -fx-padding: 20; -fx-alignment: center;");
        weatherBox.setPrefWidth(Double.MAX_VALUE);
        weatherBox.setMaxWidth(Double.MAX_VALUE);
        
        // Título "Hoje"
        Text todayText = new Text("HOJE");
        todayText.setFont(Font.font("System", FontWeight.BOLD, 18));
        todayText.setStyle("-fx-fill: white;");
        
        // Nome da cidade
        Text cityText = new Text(weatherData.getCityName());
        cityText.setFont(Font.font("System", FontWeight.BOLD, 24));
        cityText.setStyle("-fx-fill: white;");
        
        // Temperatura
        Text tempText = new Text(String.format("%.1f°C", weatherData.getTemperature()));
        tempText.setFont(Font.font("System", FontWeight.BOLD, 36));
        tempText.setStyle("-fx-fill: white;");
        
        // Descrição
        Text descText = new Text(weatherData.getDescription().toUpperCase());
        descText.setFont(Font.font("System", FontWeight.NORMAL, 16));
        descText.setStyle("-fx-fill: white;");
        
        // Ícone do tempo
        ImageView weatherIcon = new ImageView();
        weatherIcon.setFitHeight(80);
        weatherIcon.setFitWidth(80);
        weatherIcon.setPreserveRatio(true);
        
        try {
            String iconUrl = weatherService.getWeatherIconUrl(weatherData.getIcon());
            Image image = new Image(iconUrl);
            weatherIcon.setImage(image);
        } catch (Exception e) {
            Text iconText = new Text("🌤️");
            iconText.setFont(Font.font("System", 48));
            weatherBox.getChildren().add(iconText);
        }
        
        // Detalhes adicionais
        VBox detailsBox = new VBox(8);
        detailsBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                           "-fx-background-radius: 8; -fx-padding: 15; -fx-alignment: center;");
        
        Text humidityText = new Text("Umidade: " + weatherData.getHumidity() + "%");
        humidityText.setFont(Font.font("System", 14));
        humidityText.setStyle("-fx-fill: white;");
        
        Text windText = new Text(String.format("Vento: %.1f m/s", weatherData.getWindSpeed()));
        windText.setFont(Font.font("System", 14));
        windText.setStyle("-fx-fill: white;");
        
        detailsBox.getChildren().addAll(humidityText, windText);
        
        // Adicionar todos os elementos ao container
        weatherBox.getChildren().addAll(todayText, cityText, weatherIcon, tempText, descText, detailsBox);
        
        return weatherBox;
    }
    
    private VBox createForecastBox(WeatherData weatherData) {
        VBox forecastBox = new VBox(15);
        forecastBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");
        forecastBox.setPrefWidth(Double.MAX_VALUE);
        forecastBox.setMaxWidth(Double.MAX_VALUE);
        
        // Título da seção
        Text forecastTitle = new Text("PREVISÃO DOS PRÓXIMOS 3 DIAS");
        forecastTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        forecastTitle.setStyle("-fx-fill: #2c3e50;");
        
        // Grid para as previsões
        GridPane forecastGrid = new GridPane();
        forecastGrid.setHgap(15);
        forecastGrid.setVgap(10);
        forecastGrid.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < weatherData.getDailyForecasts().size(); i++) {
            DailyForecast forecast = weatherData.getDailyForecasts().get(i);
            VBox dayBox = createDayForecastBox(forecast);
            forecastGrid.add(dayBox, i, 0);
        }
        
        forecastBox.getChildren().addAll(forecastTitle, forecastGrid);
        return forecastBox;
    }
    
    private VBox createDayForecastBox(DailyForecast forecast) {
        VBox dayBox = new VBox(10);
        dayBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 15; -fx-alignment: center;");
        dayBox.setMinWidth(120);
        dayBox.setPrefWidth(120);
        
        // Data
        Text dateText = new Text(forecast.getDate());
        dateText.setFont(Font.font("System", FontWeight.BOLD, 14));
        dateText.setStyle("-fx-fill: #2c3e50;");
        
        // Ícone
        ImageView icon = new ImageView();
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        icon.setPreserveRatio(true);
        
        try {
            String iconUrl = weatherService.getWeatherIconUrl(forecast.getIcon());
            Image image = new Image(iconUrl);
            icon.setImage(image);
        } catch (Exception e) {
            Text iconText = new Text("🌤️");
            iconText.setFont(Font.font("System", 24));
            dayBox.getChildren().add(iconText);
        }
        
        // Temperaturas
        Text tempText = new Text(String.format("%.0f° / %.0f°", forecast.getMinTemp(), forecast.getMaxTemp()));
        tempText.setFont(Font.font("System", FontWeight.BOLD, 16));
        tempText.setStyle("-fx-fill: #2c3e50;");
        
        // Descrição
        Text descText = new Text(forecast.getDescription());
        descText.setFont(Font.font("System", 12));
        descText.setStyle("-fx-fill: #7f8c8d;");
        descText.setWrappingWidth(100);
        
        dayBox.getChildren().addAll(dateText, icon, tempText, descText);
        return dayBox;
    }
    
    @FXML
    private void handleEnterKey() {
        searchWeather();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 