import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AdminController {

    @FXML private ListView<Hotel> hotelListView;
    @FXML private Label hotelCountLabel;
    @FXML private Label walletLabel;
    @FXML private Label revenueLabel;
    @FXML private TextField hotelNameField, hotelAddressField;
    @FXML private ComboBox<Ratings> ratingCombo;
    @FXML private TextField hotelAdminIDField;
    private Admin admin;

    @FXML
    public void initialize() {
        admin = (Admin) Session.currentUser;
        ratingCombo.getItems().addAll(Ratings.values());
        setupHotelList(); // Helper method for the list
        onViewRevenue();  // Load money stats on startup
    }


    @FXML
    private void onCreateHotel() {
        try {
            if (Objects.equals(hotelNameField.getText(), "") || ratingCombo.getValue()==null|| Objects.equals(hotelAddressField.getText(), ""))throw new IllegalArgumentException("Just because you are super doesnt mean you are super natural!");
            admin.createHotel(hotelNameField.getText(), ratingCombo.getValue(), hotelAddressField.getText());
            HotelApplication.showAlert("Success", "Hotel Created");
            setupHotelList();
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onViewRevenue() {
        // 1. Get Wallet Balance
        double myBalance = admin.wallet.getBalance();
        walletLabel.setText(String.format("$%.2f", myBalance));

        // 2. Get Total System Revenue
        double revenue = admin.getRevenue();
        revenueLabel.setText(String.format("$%.2f", revenue));
    }

    // NEW: Setup the Fancy Hotel List
    private void setupHotelList() {
        // 1. Set the data
        hotelListView.setItems(FXCollections.observableArrayList(Database.getInstance().hotels));
        hotelCountLabel.setText(Database.getInstance().hotels.size() + " hotels");

        // 2. Custom Cell Factory (The "Card" Look)
        hotelListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Hotel hotel, boolean empty) {
                super.updateItem(hotel, empty);

                if (empty || hotel == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Create a VBox to hold the hotel info
                    VBox container = new VBox(5);
                    container.setStyle("-fx-padding: 10; -fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

                    // Top Row: Name and Stars
                    HBox topRow = new HBox(10);
                    Label nameLbl = new Label(hotel.getHotelName());
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    String starStr = switch (hotel.getRating()) {
                        case FIVE_STAR -> "★★★★★";
                        case FOUR_STAR -> "★★★★";
                        case THREE_STAR -> "★★★";
                        case TWO_STAR -> "★★";
                        case ONE_STAR -> "★";
                    };
                    Label starLbl = new Label(starStr);
                    starLbl.setStyle("-fx-text-fill: #f59e0b;"); // Gold color

                    topRow.getChildren().addAll(nameLbl, starLbl);

                    // Bottom Row: Location and Room Count
                    HBox bottomRow = new HBox(10);
                    Label locLbl = new Label("📍 " + hotel.getLocation());
                    Label roomLbl = new Label("🛏 " + hotel.getRooms().size() + " rooms");
                    locLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                    roomLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                    bottomRow.getChildren().addAll(locLbl, roomLbl);

                    container.getChildren().addAll(topRow, bottomRow);
                    setGraphic(container);
                }
            }
        });
    }

    @FXML
    private void onFlagOverdue() {
        // Call the modified method and get the count of evicted guests
        int evictedCount = admin.flagOverdueCustomers();

        String message;
        if (evictedCount > 0) {
            // Show a CRITICAL alert if guests were removed
            message = String.format("%d customers were found to be overstaying by 4+ days and have been EVICTED from the system. Their rooms are now available.", evictedCount);
            HotelApplication.showAlert("Critical Action: Eviction Complete", message);
        } else {
            // Show a simple alert if no one was removed
            message = "Overdue guests have been flagged for the day. No customers were evicted this run.";
            HotelApplication.showAlert("Action Complete", message);
        }
    }

    @FXML
    private void onLogout() throws IOException {
        Session.currentUser = null;
        HotelApplication.setRoot("login");
    }


    @FXML
    private void onGiveSalary() {
        String id = hotelAdminIDField.getText();
        if (id.isEmpty()) {
            HotelApplication.showError("Enter a Hotel Admin ID. You can't just toss cash into the wind!");
            return;
        }

        try {
            admin.giveSalary(id);
            HotelApplication.showAlert("Cash Tossed",
                    "A massive $50 has been reluctantly given to Hotel Admin ID: " + id );
            hotelAdminIDField.clear();
        } catch (Exception e) {
            HotelApplication.showError("Payment Failed: " + e.getMessage());
        }
    }

    @FXML
    private void onResign() {
        // --- 1. The Initial Threat ---
        if (showSarcasticConfirmation("1/5: Final Warning!",
                "This is your last chance to turn back. You're giving up ALL power.",
                "Do you truly wish to abandon your post as the sole System Overlord?")) {
            HotelApplication.showAlert("Relief", "Crisis averted! The system needs its Overlord.");
            return;
        }

        // --- 2. The Data Scare ---
        if (showSarcasticConfirmation("2/5: Data Integrity Threat",
                "Resigning means NO ONE can manage the system ever again.",
                "Are you prepared for the data chaos and angry guests?")) {
            HotelApplication.showAlert("Wimp Out", "Wise choice. Data integrity is for people who care.");
            return;
        }

        // --- 3. The Existential Dread ---
        if (showSarcasticConfirmation("3/5: The Real Question",
                "Is this what you really want to do with your life?",
                "Are you sure this is a well-thought-out life decision? (Yes/No)")) {
            HotelApplication.showAlert("Deep Thoughts", "Go grab a coffee. Think it over.");
            return;
        }

        // --- 4. The Sarcastic Guilt Trip ---
        if (showSarcasticConfirmation("4/5: The Hotel Admins Will Cry",
                "Remember the poor Hotel Admins? They need their $50 salary!",
                "Are you okay with leaving them penniless?")) {
            HotelApplication.showAlert("Guilt Trip Success", "They thank you for staying. For now.");
            return;
        }

        // --- 5. The System Rejection ---
        if (showSarcasticConfirmation("5/5: The System Itself Asks",
                "ERROR: Commitment Issues Detected. Please confirm final action.",
                "Last chance to click 'Cancel' and avoid the irreversible destruction of the Admin role.")) {
            HotelApplication.showAlert("Phew", "You were *this* close to freedom, but the system locked the door.");
            return;
        }

        // FINAL EXECUTION (Only reached if all 5 passed)
        try {
            admin.Resign(); //
            Session.currentUser = null;
            HotelApplication.setRoot("login");
        } catch (Exception e) {
            HotelApplication.showError("The system refuses to let you quit: " + e.getMessage());
        }
    }

    // Helper method to show confirmation dialogs
    private boolean showSarcasticConfirmation(String title, String header, String content) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(content);

        Optional<ButtonType> result = confirm.showAndWait(); // only call once
        return result.isPresent() && result.get() == ButtonType.CANCEL; // true if OK, false if Cancel
    }
    // In AdminController - make the dashboard more visual
    @FXML private Label totalGuestsLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label averageOccupancyLabel;

    private void updateDashboard() {
        totalGuestsLabel.setText(String.valueOf(Database.getInstance().guests.size()));

        int totalBookings = Database.getInstance().guests.stream()
                .mapToInt(g -> g.getRoomsReserved().size()).sum();
        totalBookingsLabel.setText(String.valueOf(totalBookings));

        // Calculate occupancy
        long totalRooms = Database.getInstance().hotels.stream()
                .mapToInt(h -> h.getRooms().size()).sum();
        long occupiedRooms = Database.getInstance().hotels.stream()
                .flatMap(h -> h.getRooms().stream())
                .filter(r -> !r.available).count();

        double occupancy = (totalRooms > 0) ? (occupiedRooms * 100.0 / totalRooms) : 0;
        averageOccupancyLabel.setText(String.format("%.1f%%", occupancy));
    }
}
