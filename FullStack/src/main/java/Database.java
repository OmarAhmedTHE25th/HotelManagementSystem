import java.io.*;
import java.util.ArrayList;
public class Database implements Serializable {
    private static  Database instance = new Database();
    private static final String FILE_NAME = "hotel_data.ser";
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
    public static void saveData() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(instance);


        } catch (IOException _) {

        }
    }


    // Loads the state from the file and replaces the current 'instance'
    public static void loadData() {
        File file = new File(FILE_NAME);
        // Only load if the save file exists
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                // Replace the static instance with the loaded object
                instance = (Database) ois.readObject();

            } catch (IOException | ClassNotFoundException _) {

            }
        }
    }
}
