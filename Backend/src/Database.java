import java.util.ArrayList;
public class Database {
    private static final Database instance = new Database();
    public static Database getInstance() {
        return instance;
    }
    ArrayList<Admin> admins = new ArrayList<>();
    ArrayList<Hotel> hotels = new ArrayList<>();
    ArrayList<Guest> guests = new ArrayList<>();

}
