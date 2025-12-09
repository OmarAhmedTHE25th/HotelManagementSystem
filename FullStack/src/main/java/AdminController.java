import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AdminController {

    @FXML private TextField hotelNameField, hotelAddressField;
    @FXML private ComboBox<Ratings> ratingCombo;
    @FXML private TextArea outputArea;
    @FXML private TextField hotelAdminIDField;
    private Admin admin;

    @FXML
    public void initialize() {
        admin = (Admin) Session.currentUser;
        ratingCombo.getItems().addAll(Ratings.values());
        updateHotelList();
    }

    @FXML
    private void onCreateHotel() {
        try {
            if (Objects.equals(hotelNameField.getText(), "") || ratingCombo.getValue()==null|| Objects.equals(hotelAddressField.getText(), ""))throw new IllegalArgumentException("Just because you are super doesnt mean you are super natural!");
            admin.createHotel(hotelNameField.getText(), ratingCombo.getValue(), hotelAddressField.getText());
            HotelApplication.showAlert("Success", "Hotel Created");
            updateHotelList();
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }

    @FXML
    private void onViewRevenue() {
        outputArea.setText("Total Revenue: $" + admin.getRevenue());
    }

    @FXML
    private void onFlagOverdue() {
        admin.flagOverdueCustomers();
        outputArea.setText("Overdue customers flagged.");
    }

    @FXML
    private void onLogout() throws IOException {
        Session.currentUser = null;
        HotelApplication.setRoot("login");
    }

    private void updateHotelList() {
        outputArea.setText(admin.viewHotels());
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
                    "A massive $50 has been reluctantly given to Hotel Admin ID: " + id + ".\n" +
                            "Your own wallet balance increased by $50 for the effort. You earned it.");
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

}
