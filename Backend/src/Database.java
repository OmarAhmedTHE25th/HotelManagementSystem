import java.util.ArrayList;
public class Database {
    private static final Database instance = new Database();
    public static Database getInstance() {
        return instance;
    }
    Admin[] admins= new Admin[1];
    ArrayList<Hotel> hotels = new ArrayList<>();
    ArrayList<Guest> guests = new ArrayList<>();
    public Admin getAdmin()
    {
        return admins[0];
    }
    public void setAdmin(Admin admin)
    {admins[0] = admin;}

}
