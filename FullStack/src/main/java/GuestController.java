import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
        hotelList.setCellFactory(_ -> new ListCell<Hotel>() {
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
    //        class ComplaintInsults {
//            private final ArrayList<String> complaintInsults;
//            private final Random random;
//
//            public ComplaintInsults() {
//                complaintInsults = new ArrayList<>();
//                random = new Random();
//                initializeComplaintInsults();
//            }
//
//            private void initializeComplaintInsults() {
//                // For General Complaints
//                complaintInsults.add("Your complaint has been filed under 'First World Problems, Volume 47'.");
//                complaintInsults.add("We've added your complaint to our 'Things That Definitely Matter' folder. (It's empty.)");
//                complaintInsults.add("Your complaint has been processed with the care and attention it deserves: None.");
//
//                // Room Complaint Insults
//                complaintInsults.add("The room is exactly as advertised: A place to sleep, not your mother's womb.");
//                complaintInsults.add("If you wanted luxury, you should have paid more than $3.50.");
//                complaintInsults.add("The Wi-Fi is fine. Maybe your attitude is blocking the signal.");
//                complaintInsults.add("The 'stain' you mentioned is actually where we cleaned up the last complainer's tears.");
//
//                // Service Complaint Insults
//                complaintInsults.add("Our staff is trained to ignore customers with your level of entitlement.");
//                complaintInsults.add("The slow service is a feature, not a bug. It weeds out the impatient.");
//                complaintInsults.add("Housekeeping skipped your room? We call that 'natural selection'.");
//
//                // Food Complaint Insults
//                complaintInsults.add("The breakfast is 'continental' which means 'better than you deserve, continental'.");
//                complaintInsults.add("If you wanted gourmet, you should have stayed home and cooked.");
//                complaintInsults.add("The coffee is weak? That's to match your personality.");
//
//                // Noise Complaint Insults
//                complaintInsults.add("It's called a 'hotel', not a 'library for delicate flowers'.");
//                complaintInsults.add("The noise complaint form is located right next to the 'Get Over It' brochure.");
//                complaintInsults.add("We'll address the noise as soon as we finish ignoring your complaint.");
//
//                // Karen Specials
//                complaintInsults.add("Let me guess: You'd like to speak to a manager who also doesn't care?");
//                complaintInsults.add("Your Yelp review is already pre-written in our 'Predictable Complaints' file.");
//                complaintInsults.add("We've forwarded your complaint to our 'Makes Us Laugh' department.");
//
//                // Hotel Policy Insults
//                complaintInsults.add("The policy exists because of people like you. Congratulations on being the reason.");
//                complaintInsults.add("Our cancellation policy is firm, much like our disregard for your complaint.");
//                complaintInsults.add("The fine print you didn't read? It says 'We don't care about your complaints'.");
//
//                // Check-out Time Complaints
//                complaintInsults.add("Check-out is at 11 AM. Your welcome expired at check-in.");
//                complaintInsults.add("The late check-out fee is $50 per hour, or $100 if you're complaining.");
//
//                // Bathroom Complaint Insults
//                complaintInsults.add("The towels are 'rough'? We call that 'exfoliating your fragile ego'.");
//                complaintInsults.add("Hot water is extra. Cold showers build character, which you clearly need.");
//
//                // Bed Complaint Insults
//                complaintInsults.add("The bed is firm because life is hard. Get used to both.");
//                complaintInsults.add("If you wanted a better mattress, you should have brought your own princess-and-the-pea setup.");
//
//                // View Complaint Insults
//                complaintInsults.add("You paid for a room, not a view. The brick wall is complimentary.");
//                complaintInsults.add("The 'ocean view' room faces the parking lot because we're realists.");
//
//                // Petty Complaint Insults
//                complaintInsults.add("The ice machine is broken? Try using your cold heart to cool your drink.");
//                complaintInsults.add("Your complaint about the artwork has been forwarded to our 'Nobody Cares' gallery.");
//
//                // Weather-Related Complaints
//                complaintInsults.add("We control many things, but the weather and your bad attitude aren't among them.");
//                complaintInsults.add("Rainy during your stay? That's nature agreeing with our assessment of you.");
//
//                // Ultimate Savage Mode
//                complaintInsults.add("Your complaint has been noted and immediately used as kindling for our staff bonfire.");
//                complaintInsults.add("We've added your photo to our 'Future Complainers' wall of shame.");
//                complaintInsults.add("The only thing needing improvement here is your judgment in hotels.");
//                complaintInsults.add("We'll address your complaint right after we finish this round of 'Who Gives a Shit?'");
//            }
//
//            // Get a random complaint insult
//            public String getRandomComplaintInsult() {
//                if (complaintInsults.isEmpty()) {
//                    return "We'd insult your complaint, but it's too pathetic even for us.";
//                }
//                return complaintInsults.get(random.nextInt(complaintInsults.size()));
//            }
//        }
//ComplaintInsults insults = new ComplaintInsults();

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
    private void onViewHistory() {
        Guest g = (Guest) Session.currentUser;

        // Use the HotelApplication's alert method to display the formatted history string
        HotelApplication.showAlert("Booking History", g.viewHistory());
    }
}


