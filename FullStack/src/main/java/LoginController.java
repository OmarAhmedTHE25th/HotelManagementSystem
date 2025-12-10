import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class LoginController {

    @FXML private TextField usernameField, idField, regUsername, regId, regPass, regHotelName;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo, regRole;
    @FXML private DatePicker regDob;
    @FXML private TabPane tabPane;




    @FXML
    public void initialize() {
        // Initialize login role combo
        if (roleCombo != null) {
            roleCombo.getItems().addAll("Guest", "Admin", "Hotel Admin");
            roleCombo.setValue("Guest");
        }

        // Initialize registration role combo
        if (regRole != null) {
            regRole.getItems().addAll("Guest", "Admin", "Hotel Admin");
            regRole.setValue("Guest");

            // Initialize the field to be hidden AND unmanaged (so it takes no space)
            if (regHotelName != null) {
                regHotelName.setVisible(false);
                regHotelName.setManaged(false); // Collapses the space
            }

            regRole.valueProperty().addListener((_, _, newVal) -> {
                if (regHotelName == null) return;

                if ("Hotel Admin".equals(newVal)) {
                    // Show the field
                    regHotelName.setVisible(true);
                    regHotelName.setManaged(true); // Allocate space for it
                    regHotelName.setDisable(false); // Make it typable
                } else {
                    // Hide the field
                    regHotelName.setVisible(false);
                    regHotelName.setManaged(false); // Remove space
                    regHotelName.setDisable(true);
                }
            });
        }

    }

    @FXML
    private void onLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String id = idField.getText();
        String role = roleCombo.getValue();

        // FIX 1: Prevent crash if no role is selected
        if (role == null) {
            HotelApplication.showError("Please select a role (Guest, Admin, etc)");
            return;
        }

        try {
            switch (role) {
                case "Guest" -> {
                    // Check credentials
                    if (new Guest().logIn(user, pass, id)) {
                        // Attempt to find the user object
                        for (Guest g : Database.getInstance().guests) {
                            if (g.ID.equals(id)) {
                                Session.currentUser = g;
                                break;
                            }
                        }

                        // FIX 2: Only change screen if we ACTUALLY found the user
                        if (Session.currentUser != null) {
                            HotelApplication.setRoot("guest");
                        } else {
                            HotelApplication.showError("Login successful, but User ID not found in database.");
                        }
                    } else {
                        HotelApplication.showError("Invalid Credentials");
                    }
                }
                case "Admin" -> {
                    Admin admin = Database.getInstance().getAdmin();
                    if (admin != null && admin.logIn(user, pass, id)) {
                        Session.currentUser = admin;
                        HotelApplication.setRoot("admin");
                    } else {
                        HotelApplication.showError("Invalid Admin Credentials");
                    }
                }
                case "Hotel Admin" -> {
                    if (new HotelAdmin().logIn(user, pass, id)) {
                        for (HotelAdmin ha : Database.getInstance().hotelAdmins) {
                            if (ha.ID.equals(id)) {
                                Session.currentUser = ha;
                                break;
                            }
                        }

                        // FIX 2: Same check here for Hotel Admin
                        if (Session.currentUser != null) {
                            HotelApplication.setRoot("hotel_admin");
                        } else {
                            HotelApplication.showError("Login successful, but User ID not found.");
                        }
                    } else {
                        HotelApplication.showError("Invalid Hotel Admin Credentials");
                    }
                }
            }
        } catch (Exception e) {
             // Print the real error to console
            HotelApplication.showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        try {
            String user = regUsername.getText();
            String pass = regPass.getText();
            String id = regId.getText();
            LocalDate dob = regDob.getValue();
            String role = regRole.getValue();

            if (dob == null) throw new IllegalArgumentException("Select Date of Birth");

            if ("Guest".equals(role)) {
                Guest.signUp(user, pass, dob, id);
            } else if ("Admin".equals(role)) {
                Admin.signUp(user, pass, dob, id);
            } else if ("Hotel Admin".equals(role)) {
                String hName = regHotelName.getText();
                Hotel targetHotel = null;
                for (Hotel h : Database.getInstance().hotels) {
                    if (h.getHotelName().equals(hName)) {
                        targetHotel = h;
                        break;
                    }
                }
                if (targetHotel == null) throw new IllegalArgumentException("Hotel not found");

                HotelAdmin.signUp(user, pass, dob, id, targetHotel);
            }

            HotelApplication.showAlert("Success", "Account Created! Please Login.");
            tabPane.getSelectionModel().select(0);
        } catch (Exception e) {
            HotelApplication.showError(e.getMessage());
        }
    }
}