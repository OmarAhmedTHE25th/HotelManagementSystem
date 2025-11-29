import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;

import java.io.IOException;

public class HotelAdminController {

    @FXML private Label hotelNameLabel;
    @FXML private Label hotelLocationLabel;

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

        hotelNameLabel.setText(hotel.getHotelName());
        hotelLocationLabel.setText("(" + hotel.getLocation() + ")");

        // Fill table
        roomNumberCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().roomNumber));
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().price));
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().roomType.name()));

        roomsTable.setItems(FXCollections.observableArrayList(hotel.getRooms()));

        // Room type dropdown:
        newRoomTypeBox.getItems().addAll(Types.values());
    }

    @FXML
    private void onAddRoom() {
        try {
            int num = Integer.parseInt(newRoomNumberField.getText());
            double price = Double.parseDouble(newRoomPriceField.getText());
            Types type = newRoomTypeBox.getValue();

            admin.createRoom(num, price, type);

            roomsTable.getItems().setAll(admin.getHotel().getRooms());
            HotelApplication.showAlert("Success", "Room Added!");
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

            roomsTable.getItems().setAll(admin.getHotel().getRooms());
            HotelApplication.showAlert("Success", "Price Updated!");
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
