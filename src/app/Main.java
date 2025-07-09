package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource("/ui/views/Login.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Arquivo FXML não encontrado!");
            }
            
            System.out.println("Carregando FXML de: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);

            Scene scene = new Scene(root);
        
            primaryStage.setScene(scene);
            primaryStage.setTitle("PlantCare");
            primaryStage.setFullScreen(true); // Tela cheia REAL (oculta a barra do sistema)
            primaryStage.show();
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