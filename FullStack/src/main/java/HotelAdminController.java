import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.collections.*;

import java.io.IOException;

public class HotelAdminController {

    @FXML private PieChart occupancyChart;
    @FXML private Label hotelNameLabel;
    @FXML private Label hotelLocationLabel;
    @FXML private Label roomCountLabel;
    @FXML private Label totalRoomsLabel;
    @FXML private Label availableRoomsLabel;
    @FXML private Label occupiedRoomsLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Number> roomNumberCol;
    @FXML private TableColumn<Room, Number> priceCol;
    @FXML private TableColumn<Room, String> typeCol;

    @FXML private TextField newRoomNumberField;
    @FXML private TextField newRoomPriceField;
    @FXML private ComboBox<Types> newRoomTypeBox;

    @FXML private TextField priceRoomNumberField;
    @FXML private TextField newPriceField;

    private HotelAdmin admin;

    @FXML
    public void initialize() {
        admin = (HotelAdmin) Session.currentUser;
        Hotel hotel = admin.getHotel();
        admin.checkPaymentStatus();
        // Set hotel info
        hotelNameLabel.setText(hotel.getHotelName());
        hotelLocationLabel.setText(hotel.getLocation());

        // Setup table columns
        roomNumberCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().roomNumber));
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().price));
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().roomType.name()));

        // Load rooms
        refreshRoomList();

        // Fill room type dropdown
        newRoomTypeBox.getItems().addAll(Types.values());

        // Update stats
        updateStats();
    }

    private void refreshRoomList() {
        roomsTable.setItems(FXCollections.observableArrayList(admin.getHotel().getRooms()));
        updateStats();
    }
    private void updateStats() {
            Hotel hotel = admin.getHotel();
            if (hotel == null) return; // Safety check

            // Create data for the chart
            int total = hotel.getRooms().size();

            // Calculate available rooms safely
            long available = hotel.getRooms().stream()
                    .filter(r -> r.available)
                    .count();

            long occupied = total - available;

            // Update the header label (The one in your screenshot)
            if (roomCountLabel != null) {
                roomCountLabel.setText(total + " rooms");
            }

            // Update the big stats cards
            if (totalRoomsLabel != null) {
                totalRoomsLabel.setText(String.valueOf(total));
            }
            if (availableRoomsLabel != null) {
                availableRoomsLabel.setText(String.valueOf(available));
            }
            if (occupiedRoomsLabel != null) {
                occupiedRoomsLabel.setText(String.valueOf(occupied));
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Available", available),
                    new PieChart.Data("Occupied", occupied)
            );

            occupancyChart.setData(pieData);

    }

    @FXML
    private void onAddRoom() {
        try {
            int num = Integer.parseInt(newRoomNumberField.getText());
            double price = Double.parseDouble(newRoomPriceField.getText());
            Types type = newRoomTypeBox.getValue();

            if (type == null) {
                HotelApplication.showError("Please select a room type");
                return;
            }

            admin.createRoom(num, price, type);
            refreshRoomList();

            HotelApplication.showAlert("Success", "Room #" + num + " added successfully!");

            // Clear fields
            newRoomNumberField.clear();
            newRoomPriceField.clear();
            newRoomTypeBox.setValue(null);
            roomsTable.refresh();
        } catch (NumberFormatException e) {
            HotelApplication.showError("Please enter valid numbers for room number and price");
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onChangePrice() {
        try {
            int num = Integer.parseInt(priceRoomNumberField.getText());
            int newP = Integer.parseInt(newPriceField.getText());

            admin.changeRoomPrice(num, newP);
            refreshRoomList();

            HotelApplication.showAlert("Success", "Room #" + num + " price updated to $" + newP);

            // Clear fields
            priceRoomNumberField.clear();
            newPriceField.clear();
            roomsTable.refresh();
        } catch (NumberFormatException e) {
            HotelApplication.showError("Please enter valid numbers");
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onLogout() throws IOException {
        Session.currentUser = null;
        HotelApplication.setRoot("login");
    }

}
