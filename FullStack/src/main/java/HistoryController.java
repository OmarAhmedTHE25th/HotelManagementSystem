import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class HistoryController {

    // FXML TableView and Columns (must match history_view.fxml)
    @FXML private TableView<Room> historyTable;
    @FXML private TableColumn<Room, String> colHotel;
    @FXML private TableColumn<Room, Number> colRoom;
    @FXML private TableColumn<Room, Number> colPrice;
    @FXML private TableColumn<Room, String> colCheckout;
    @FXML private TableColumn<Room, String> colStatus;

    // This method is called from GuestController to pass the history data
    public void setHistoryData(List<Room> history) {

        // 1. Setup column bindings
        colHotel.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().hotel.getHotelName()));
        colRoom.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().roomNumber));
        colCheckout.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().checkout.toString()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().status.toString()));

        // Price Calculation Logic
        colPrice.setCellValueFactory(cell -> {
            LocalDate today = LocalDate.now();
            LocalDate checkout = cell.getValue().checkout;
            double pricePerDay = cell.getValue().price;

            // Calculate total days for the booking: Max of 1 day, or days between now and checkout.
            // This is a simplified way to represent the total reservation value from the snapshot.
            long days = ChronoUnit.DAYS.between(today, checkout);
            double totalPrice = pricePerDay * Math.max(1, days);

            return new SimpleDoubleProperty(totalPrice);
        });

        // 2. Load data into table
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    @FXML
    private void onClose() {
        // Find the window (Stage) and close it
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}