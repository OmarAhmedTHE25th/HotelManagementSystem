import java.util.ArrayList;
public class Database {
    private static final Database instance = new Database();
    public static Database getInstance() {
        return instance;
    }
   private final Admin[] admin= new Admin[1];
    ArrayList<Hotel> hotels = new ArrayList<>();
    ArrayList<Guest> guests = new ArrayList<>();
    ArrayList<HotelAdmin> hotelAdmins = new ArrayList<>();
    public Admin getAdmin()
    {
        return admin[0];
    }
    public void setAdmin(Admin adminObj)
    {admin[0] = adminObj;}

}
