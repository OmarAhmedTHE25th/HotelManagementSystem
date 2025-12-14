import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox; // Import for the root of the new FXML

import javax.swing.*;

public class GuestController {

    @FXML private Label totalPriceLabel;
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
    @FXML private ComboBox<Hotel> hotelActionCombo;
    @FXML private TextField searchField;
    @FXML private ComboBox<Ratings> ratingFilterCombo;
    private Guest guest;
    @FXML private TableColumn<Room, String> resHotelCol;
    @FXML
    public void initialize() {
        guest = (Guest) Session.currentUser;
        welcomeLabel.setText("Welcome, " + guest.username);
        updateWallet();

        ratingFilterCombo.getItems().addAll(Ratings.values()); //

        // Initial load of all hotels using the new helper function
        refreshHotelList(null);
// Listen for when the user picks a date
        checkoutDate.valueProperty().addListener((_, _, _) -> updatePrice());

// Listen for when the user picks a room from the table
        roomTable.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> updatePrice());
        // We only need to populate the action combo with ALL hotels once
        hotelActionCombo.setItems(FXCollections.observableArrayList(Database.getInstance().hotels));
        hotelList.setItems(FXCollections.observableArrayList(Database.getInstance().hotels));
        hotelActionCombo.setItems(FXCollections.observableArrayList(Database.getInstance().hotels));
        // 1. Customize how items look in the dropdown list (Cell Factory)
        hotelActionCombo.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Hotel item, boolean empty) {
                super.updateItem(item, empty);
                // If the item exists, set the text to ONLY the hotel name
                setText(empty ? null : item.getHotelName()); //
            }
        });

        // 2. Customize how the selected item looks in the combo box button (Button Cell)
        hotelActionCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Hotel item, boolean empty) {
                super.updateItem(item, empty);
                // If the item exists, set the text to ONLY the hotel name
                setText(empty ? null : item.getHotelName()); //
            }
        });
        hotelList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Hotel hotel, boolean empty) {
                super.updateItem(hotel, empty);

                if (empty || hotel == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    String stars = String.valueOf(switch (hotel.getRating()) {
                        case Ratings.FIVE_STAR -> "★★★★★";
                        case Ratings.FOUR_STAR -> "★★★★";
                        case Ratings.THREE_STAR -> "★★★";
                        case Ratings.TWO_STAR -> "★★";
                        case ONE_STAR -> "★";
                    });

                    setText(hotel.getHotelName() + "\n" + stars + " • " + hotel.getAddress());
                    setStyle("-fx-padding: 8; -fx-font-size: 14; -fx-font-weight: 500;");
                }
            }
        });

        hotelList.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
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
        resHotelCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().hotel.getHotelName()));
        resRoomCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().roomNumber));

        resPriceCol.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().price* ChronoUnit.DAYS.between(LocalDate.now(),cell.getValue().checkout)));

        resCheckoutCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().checkout.toString()));


        refreshReservations();
    }

    private void loadRooms(@NotNull Hotel hotel) {
        roomTable.setItems(FXCollections.observableArrayList(
                hotel.getRooms().stream().filter(r -> r.available).toList()
        ));
    }

    @FXML
    private void onSearch() {
        try {
            String query = searchField.getText();
            Ratings minRating = ratingFilterCombo.getValue();

            // Call the improved search method from Guest.java
            List<Hotel> results = guest.searchHotels(query, minRating);

            // Update the ListView with the search results
            refreshHotelList(results);

            HotelApplication.showAlert("Search Complete", "Found " + results.size() + " hotels matching your criteria.");

        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    // --- ADD/MODIFY HELPER METHOD ---
// This method is now used for both initial load and search results
    private void refreshHotelList(List<Hotel> results) {
        if (results == null) {
            // Load all hotels if no results are provided (initial load)
            results = Database.getInstance().hotels;
        }

        // Update the main hotel list
        hotelList.setItems(FXCollections.observableArrayList(results));
    }

    static class Insults {
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
    @FXML
    private void onReserve() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        LocalDate date = checkoutDate.getValue();

        if (selected == null || date == null) {
            HotelApplication.showError("Select a room and checkout date");
            return;
        }

        try {
            Insults insult = new Insults();
            if (guest.wallet.getBalance() < selected.price)throw new IllegalArgumentException("No Poor People Allowed!");
            if(guest.getRoomsReserved().size() >= 5) {HotelApplication.showAlert("Too many rooms", insult.getRandomInsult());return;}
            double totalprice = selected.price;
            if (Math.random() < 0.1){HotelApplication.showAlert("Inflation", "Your ugly face made the room Gods angry, +50$ fees");selected.price+=50;}
            if (Math.random() < 0.01){HotelApplication.showAlert("Pity", "We feel pity for your sorry ass. 10$ discount");selected.price-=10;}
            if (Math.random() < 0.04){HotelApplication.showAlert("Judgment", "The system crashed in your imagination. -0$ but we’re judging you.");}
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

        // 2. Get the selected hotel
        Hotel selectedHotel = hotelActionCombo.getValue();
        if (selectedHotel == null) {
            HotelApplication.showError("Select a Hotel first for the action.");
            return;
        }

        try {
            int roomNumber = Integer.parseInt(roomActionField.getText());

            // 3. Pass the Hotel object to the backend
            g.cancelReservation(roomNumber, selectedHotel); // <--- UPDATED CALL

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

        // 2. Get the selected hotel
        Hotel selectedHotel = hotelActionCombo.getValue();
        if (selectedHotel == null) {
            HotelApplication.showError("Select a Hotel first for the action.");
            return;
        }

        try {
            int roomNumber = Integer.parseInt(roomActionField.getText());

            // 3. Pass the Hotel object to the backend
            if (g.checkout(roomNumber, selectedHotel)) { // <--- UPDATED CALL
                HotelApplication.showAlert("Checkout Complete", "You have checked out successfully!");
                refreshReservations();
            } else {
                HotelApplication.showError("Invalid room number in " + selectedHotel.getHotelName());
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
            if(amount >= 1000000)
            {
                HotelApplication.showError("Stop Lying.");
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


            String complaint = result.trim();
            int length = complaint.length();

            if (length == 0) {
                HotelApplication.showAlert("Complaint Rejected", "You didn't type anything. We cannot process your silence.");
                return;
            }

            if (length < 20 || length > 200) {

                class Insults {
                    private final ArrayList<String> insults;
                    private final Random random;

                    public Insults() {

                        insults = new ArrayList<>();
                        random = new Random();
                        // Example:
                        insults.add("Your complaint is too long; \nwe ran out of patience. Denied.");
                        insults.add("Too brief. Put more effort into your misery. \nTry again.");
                        insults.add("We only accept complaints formatted in haiku. Rejected.");

                    }
                    public String getRandomInsult() {
                        return insults.get(random.nextInt(insults.size()));
                    }
                }

                String rejectionMessage = (length < 20)
                        ? "Your complaint of " + length + " characters is offensively short. "
                        : "Your complaint of " + length + " characters is offensively long. ";

                HotelApplication.showAlert("Complaint Rejected!",
                        rejectionMessage + "\n\nSystem Response: " + new Insults().getRandomInsult());
                return;
            }


            HotelApplication.showAlert("Complaint", "Thank for your time. Your complaint will be processed soon :-}");
        });
    }
    @FXML
    private void onViewHistory() throws IOException {
        Guest g = (Guest) Session.currentUser;


            // 1. Load the FXML file for the history table view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/history.fxml"));
            VBox root = loader.load();

            // 2. Get the new controller and pass the data (history list)
            HistoryController historyController = loader.getController();
            // Pass the list of Room snapshots
            historyController.setHistoryData(g.getHistory());

            // 3. Create a new stage (pop-up window)
            Stage historyStage = new Stage();
            historyStage.setTitle("Booking History");
            // Modality.APPLICATION_MODAL means you must close this window before using the main app
            historyStage.initModality(Modality.APPLICATION_MODAL);

            // Load the scene and apply the style sheet for a consistent look
            Scene scene = new Scene(root, 700, 500);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

            historyStage.setScene(scene);
            historyStage.showAndWait();


    }
    private void updatePrice() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        LocalDate date = checkoutDate.getValue();
        LocalDate today = LocalDate.now();

        if (selectedRoom != null && date != null && date.isAfter(today)) {
            long days = ChronoUnit.DAYS.between(today, date);
            double total = selectedRoom.price * days;
            totalPriceLabel.setText("Total: $" + total);
        } else {
            totalPriceLabel.setText("Total: $0.00");
        }
    }
}


