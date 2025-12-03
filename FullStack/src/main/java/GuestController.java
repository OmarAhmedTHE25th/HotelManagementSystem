import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;


public class GuestController {

    @FXML private Label welcomeLabel, balanceLabel;
    @FXML private ListView<Hotel> hotelList;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> typeCol;
    @FXML private TableColumn<Room, Double> priceCol;
    @FXML private TableColumn<Room, Integer> numberCol;
    @FXML private DatePicker checkoutDate;
    @FXML private TextArea myReservationsArea;
    @FXML private TableView<Room> reservationsTable;
    @FXML private TableColumn<Room, Number> resRoomCol;
    @FXML private TableColumn<Room, Number> resPriceCol;
    @FXML private TableColumn<Room, String> resCheckoutCol;
    @FXML private TextField roomActionField;

    private Guest guest;

    @FXML
    public void initialize() {
        guest = (Guest) Session.currentUser;
        welcomeLabel.setText("Welcome, " + guest.username);
        updateWallet();

        hotelList.setItems(FXCollections.observableArrayList(Database.getInstance().hotels));

        hotelList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    guest.chooseHotel(newVal.getHotelName());
                    loadRooms(newVal);
                } catch (Exception e) {
                    HotelApplication.showError(e.getMessage());
                }
            }
        });

        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().roomType.toString()));
        priceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().price).asObject());
        numberCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().roomNumber).asObject()
        );
        resRoomCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().roomNumber));

        resPriceCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().price));

        resCheckoutCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().checkout.toString()));


        refreshReservations();
    }

    private void loadRooms(Hotel hotel) {
        roomTable.setItems(FXCollections.observableArrayList(
                hotel.getRooms().stream().filter(r -> r.available).toList()
        ));
    }

    @FXML
    private void onReserve() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        LocalDate date = checkoutDate.getValue();

        if (selected == null || date == null) {
            HotelApplication.showError("Select a room and checkout date");
            return;
        }

        try {
            if (guest.wallet.getBalance() < selected.price)throw new IllegalArgumentException("No Poor People Allowed!");
            guest.makeReservation(selected.roomNumber, date);
            HotelApplication.showAlert("Success", "Room Reserved!");
            loadRooms(selected.hotel);
            refreshReservations();
            updateWallet();
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onAddMoney() {
        HotelApplication.showAlert("Info", "Please contact admin to add funds.");
    }
    @FXML
    private void onCancelReservation() {
        Guest g = (Guest) Session.currentUser;

        try {
            int roomNumber = Integer.parseInt(roomActionField.getText());

            g.cancelReservation(roomNumber);

            HotelApplication.showAlert("Canceled", "Reservation canceled. Refund issued if applicable.");

            reservationsTable.getItems().setAll(g.getRoomsReserved());
            updateWallet();
            refreshReservations();
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }
    @FXML
    private void onCheckoutRoom() {
        Guest g = (Guest) Session.currentUser;

        try {
            int roomNumber = Integer.parseInt(roomActionField.getText());

            if (g.checkout(roomNumber)) {
                HotelApplication.showAlert("Checkout Complete", "You have checked out successfully!");
                refreshReservations();
            } else {
                HotelApplication.showError("Invalid room number");
            }

            reservationsTable.getItems().setAll(g.getRoomsReserved());
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onLogout() throws IOException {
        Session.currentUser = null;
        HotelApplication.setRoot("login");
    }

    private void refreshReservations() {
        // Update TextArea
        myReservationsArea.setText(guest.viewReservations());

        // Update TableView
        reservationsTable.getItems().setAll(guest.getRoomsReserved());
    }



    private void updateWallet() {
        Guest g = (Guest) Session.currentUser;
        balanceLabel.setText("Wallet: " + g.wallet.getBalance() + " $");
    }
    @FXML
    private TextField depositField;

    @FXML
    private void onDeposit() {
        try {
            double amount = Double.parseDouble(depositField.getText());
            if (amount <= 0) {
                HotelApplication.showError("Amount must be positive.");
                return;
            }

            Guest g = (Guest) Session.currentUser;
            g.wallet.getMoney(amount);

            HotelApplication.showAlert("Success", "Money added!");
            updateWallet();

            depositField.clear();
        } catch (Exception e) {
            HotelApplication.showError("Invalid amount.");
        }
    }
@FXML
private void onComplain()
{
    HotelApplication.showAlert("Complaint","Thank for your time your complaint will be processed soon {-:-}");
}

}
