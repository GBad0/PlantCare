package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Carrega o FXML usando o classloader
            URL fxmlUrl = getClass().getResource("/ui/views/Login.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Arquivo FXML não encontrado!");
            }
            
            System.out.println("Carregando FXML de: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);
            
            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("PlantCare - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}