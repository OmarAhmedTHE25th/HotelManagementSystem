import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

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
                new SimpleDoubleProperty(cell.getValue().price* ChronoUnit.DAYS.between(LocalDate.now(),cell.getValue().checkout)));

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
            class Insults {
                private final ArrayList<String> insults;
                private final Random random;

                public Insults() {
                    insults = new ArrayList<>();
                    random = new Random();
                    initializeInsults();
                }

                private void initializeInsults() {
                    // Brutally Honest Insults
                    insults.add("Even a goldfish has better planning skills. Maximum is 5 rooms.");
                    insults.add("Congratulations! You've discovered the boundary between 'guest' and 'nuisance'.");
                    insults.add("Your ambition exceeds your intelligence. 5 rooms. Not 6. Count them on your fingers if needed.");

                    // Sarcastic & Condescending
                    insults.add("Oh, trying to book the entire hotel? How adorable. The limit is 5.");
                    insults.add("Did you mistake this for your personal palace? We have standards. 5 rooms max.");
                    insults.add("Even a circus needs fewer clowns than rooms you're trying to book.");

                    // Short & Savage One-Liners
                    insults.add("Room greed detected. Rejected.");
                    insults.add("No.");
                    insults.add("Are you opening a zoo? 5 rooms max.");
                    insults.add("Your entitlement is showing. 5 rooms.");
                    insults.add("This isn't a timeshare scam. 5 rooms.");

                    // Hotel-Themed Roasts
                    insults.add("We're a hotel, not your inheritance. 5 rooms maximum.");
                    insults.add("The fire marshal says 'no' and frankly, so do we.");
                    insults.add("Even wedding parties have less drama than you. 5 rooms.");
                    insults.add("Housekeeping already hates you. Don't make it worse.");

                    // Creative & Unhinged
                    insults.add("ERROR: Delusions of grandeur detected. Maximum capacity: 5 rooms.");
                    insults.add("Booking failed. Your imaginary entourage doesn't need real rooms.");
                    insults.add("If you need more than 5 rooms, your family reunion has deeper issues.");
                    insults.add("We count rooms here, not your poor life choices. 5 max.");

                    // For Maximum Chaos (School Project Edition)
                    insults.add("Professor Johnson warned us about students like you. 5 rooms.");
                    insults.add("This isn't the group project you can freeload on. 5 rooms.");
                    insults.add("Your grade on this project would be higher than your room request. Denied.");

                    // Bonus Insults (because why not?)
                    insults.add("The only thing overflowing here is your audacity. 5 rooms.");
                    insults.add("We have a VIP section, but you're not in it. 5 rooms max.");
                    insults.add("Even my code has more common sense than you. Maximum: 5 rooms.");
                    insults.add("Do you also try to order 12 drinks at a bar? 5 rooms. Period.");
                    insults.add("The hotel's capacity is less than your ego. 5 rooms.");
                }

                // Get a random insult
                public String getRandomInsult() {
                    if (insults.isEmpty()) {
                        return "I'd insult you but I'm out of creativity. Also, 5 rooms max.";
                    }
                    return insults.get(random.nextInt(insults.size()));
                }

            }
            class SimpleBinary {
                // As a static method
                public static int get01() {
                    return new Random().nextInt(2);
                }

                // Or even simpler
                public static int flip() {
                    return Math.random() < 0.5 ? 0 : 1;
                }
            }
            Insults insult = new Insults();

            if (guest.wallet.getBalance() < selected.price)throw new IllegalArgumentException("No Poor People Allowed!");
            if (SimpleBinary.flip() == 1){HotelApplication.showAlert("A Ghost", "Too bad a ghost came first,good luck next time :-}");return;}
            if(guest.getRoomsReserved().size() >= 5) {HotelApplication.showAlert("Too many rooms", insult.getRandomInsult());return;}
            guest.makeReservation(selected.roomNumber, date);
            HotelApplication.showAlert("Success", "Room Reserved!");

            loadRooms(selected.hotel);
            refreshReservations();
            updateWallet();
        }



        catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onCancelReservation() {
        Guest g = (Guest) Session.currentUser;

        try {
            int roomNumber = Integer.parseInt(roomActionField.getText());

            g.cancelReservation(roomNumber);

            HotelApplication.showAlert("Canceled", "Reservation canceled. Refund issued.");

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
    private void onComplain() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Complaint");
        dialog.setHeaderText("Enter your complaint below:");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Type your complaint here...");
        textArea.setWrapText(true);

        dialog.getDialogPane().setContent(textArea);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return textArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (!result.trim().isEmpty()) {
                HotelApplication.showAlert("Complaint", "Thank for your time. Your complaint will be processed soon :-}");
            }
        });
    }

}


