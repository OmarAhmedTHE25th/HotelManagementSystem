import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class AdminController {

    @FXML private TextField hotelNameField, hotelAddressField;
    @FXML private ComboBox<Ratings> ratingCombo;
    @FXML private TextArea outputArea;

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
}
