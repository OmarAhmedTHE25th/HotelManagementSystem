import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import javafx.scene.image.Image;
public class HotelApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        seedData();
        scene = new Scene(loadFXML("login"), 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setTitle("Hotel Management System");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/img.png")).toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HotelApplication.class.getResource("/" + fxml + ".fxml"));
        return fxmlLoader.load();
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

        // --- ADMINS FOR TESTING ---
        try {
            // Main System Admin (Must have a strong password or use "devpass")
            // A strong password: "AstrongPass1!"
            Admin.signUp("superadmin", "AstrongPass1!", LocalDate.of(1980, 1, 1), "A1");
        } catch (Exception ignored) {}

        // --- Hotel 1 (Grand Plaza) ---
        Hotel h1 = new Hotel("Grand Plaza", Ratings.FIVE_STAR, "New York");
        h1.getRooms().add(new Room(101, 200.0, Types.SINGLE, h1));
        h1.getRooms().add(new Room(102, 350.0, Types.DOUBLE, h1));
        h1.getRooms().add(new Room(201, 500.0, Types.SUITE, h1));
        // New Rooms for H1
        h1.getRooms().add(new Room(202, 500.0, Types.SUITE, h1));
        h1.getRooms().add(new Room(301, 800.0, Types.DELUXE, h1));


        // Hotel Admin for h1 (must be signed up after the hotel is created)
        try {
            // A strong password: "HotelPass1#"
            HotelAdmin.signUp("hadmin1", "HotelPass1#", LocalDate.of(1990, 10, 10), "HA1", h1);
        } catch (Exception ignored) {}

        // --- Hotel 2 (Sunrise Retreat) ---
        Hotel h2 = new Hotel("Sunrise Retreat", Ratings.FOUR_STAR, "Los Angeles");
        h2.getRooms().add(new Room(301, 180.0, Types.SINGLE, h2));
        h2.getRooms().add(new Room(302, 250.0, Types.DOUBLE, h2));
        h2.getRooms().add(new Room(401, 400.0, Types.SUITE, h2));
        // New Rooms for H2
        h2.getRooms().add(new Room(303, 190.0, Types.SINGLE, h2));
        h2.getRooms().add(new Room(402, 450.0, Types.SUITE, h2));


        // --- Hotel 3 (Ocean Breeze Resort) ---
        Hotel h3 = new Hotel("Ocean Breeze Resort", Ratings.FIVE_STAR, "Miami");
        h3.getRooms().add(new Room(501, 220.0, Types.SINGLE, h3));
        h3.getRooms().add(new Room(502, 330.0, Types.DOUBLE, h3));
        h3.getRooms().add(new Room(503, 700.0, Types.SUITE, h3));
        h3.getRooms().add(new Room(601, 1000.0, Types.DELUXE, h3));
        // New Rooms for H3
        h3.getRooms().add(new Room(701, 1200.0, Types.DELUXE, h3));
        h3.getRooms().add(new Room(801, 900.0, Types.SUITE, h3));


        // --- Hotel 4 (Mountain View Lodge) ---
        Hotel h4 = new Hotel("Mountain View Lodge", Ratings.THREE_STAR, "Denver");
        h4.getRooms().add(new Room(101, 120.0, Types.SINGLE, h4));
        h4.getRooms().add(new Room(102, 180.0, Types.DOUBLE, h4));
        h4.getRooms().add(new Room(201, 250.0, Types.SUITE, h4));
        // New Rooms for H4
        h4.getRooms().add(new Room(103, 120.0, Types.SINGLE, h4));
        h4.getRooms().add(new Room(202, 250.0, Types.SUITE, h4));


        // --- NEW HOTEL 5 (The Low-Budget Option) ---
        Hotel h5 = new Hotel("Star Gazer Inn", Ratings.ONE_STAR, "Rural Area");
        h5.getRooms().add(new Room(101, 50.0, Types.STANDARD, h5));
        h5.getRooms().add(new Room(102, 75.0, Types.SINGLE, h5));
        // New Rooms for H5
        h5.getRooms().add(new Room(103, 75.0, Types.SINGLE, h5));

        // --- NEW HOTEL 6 (The Ultra-Expensive Option) ---
        Hotel h6 = new Hotel("Sky High Tower", Ratings.FIVE_STAR, "Chicago");
        h6.getRooms().add(new Room(601, 1500.0, Types.PENTHOUSE, h6));
        h6.getRooms().add(new Room(602, 1200.0, Types.DELUXE, h6));
        // New Rooms for H6
        h6.getRooms().add(new Room(603, 1500.0, Types.PENTHOUSE, h6));
        h6.getRooms().add(new Room(604, 1200.0, Types.DELUXE, h6));


        // --- GUESTS FOR TESTING ---
        try {
            // Existing Guest
            Guest.signUp("guest", "devpass", LocalDate.of(1995, 5, 20), "G1");

            // New Standard Test Guest
            Guest.signUp("testguest", "devpass", LocalDate.of(2000, 7, 15), "G2");

            // New VIP Test Guest
            Guest.signUp("vipguest", "devpass", LocalDate.of(1975, 5, 20), "G3");

            HotelAdmin.signUp("Hadmin","devpass",LocalDate.of(1990,8,3),"H1",h1);

        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        launch();
    }
}
