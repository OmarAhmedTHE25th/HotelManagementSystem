import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public class HotelApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        seedData();
        scene = new Scene(loadFXML("login"), 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HotelApplication.class.getResource("/" + fxml + ".fxml"));
            return fxmlLoader.load();
        } catch (Exception e) {
            throw e;
        }
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void seedData() {


        // --- Hotel 1 ---
        Hotel h1 = new Hotel("Grand Plaza", Ratings.FIVE_STAR, "New York");
        h1.getRooms().add(new Room(101, 200.0, Types.SINGLE, h1));
        h1.getRooms().add(new Room(102, 350.0, Types.DOUBLE, h1));
        h1.getRooms().add(new Room(201, 500.0, Types.SUITE, h1));

        // --- Hotel 2 ---
        Hotel h2 = new Hotel("Sunrise Retreat", Ratings.FOUR_STAR, "Los Angeles");
        h2.getRooms().add(new Room(301, 180.0, Types.SINGLE, h2));
        h2.getRooms().add(new Room(302, 250.0, Types.DOUBLE, h2));
        h2.getRooms().add(new Room(401, 400.0, Types.SUITE, h2));

        // --- Hotel 3 ---
        Hotel h3 = new Hotel("Ocean Breeze Resort", Ratings.FIVE_STAR, "Miami");
        h3.getRooms().add(new Room(501, 220.0, Types.SINGLE, h3));
        h3.getRooms().add(new Room(502, 330.0, Types.DOUBLE, h3));
        h3.getRooms().add(new Room(503, 700.0, Types.SUITE, h3));
        h3.getRooms().add(new Room(601, 1000.0, Types.DELUXE, h3));

        // --- Hotel 4 ---
        Hotel h4 = new Hotel("Mountain View Lodge", Ratings.THREE_STAR, "Denver");
        h4.getRooms().add(new Room(701, 120.0, Types.SINGLE, h4));
        h4.getRooms().add(new Room(702, 180.0, Types.DOUBLE, h4));
        h4.getRooms().add(new Room(801, 250.0, Types.SUITE, h4));

        // --- Guest ---
        try {
            Guest.signUp("guest", "password!@#", LocalDate.of(1995, 5, 20), "G1");
        } catch (Exception ignored) {}
    }


    public static void main(String[] args) {
        launch();
    }
}
