package Database;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import album.*;
import javafx.scene.image.Image;

/**
 * this does this serialization for the Users
 * so that when the program is closed the users and all the infor related to that user doesnt
 * get lost and can still be recovered when restarting the program
 * @author Michael cyriac
 * @autho Russell NG
 *
 */
public class Database implements Serializable{
    public static final String storeDir = "data";
    public static final String storeFile = "database.dat";
    static final long serialVersionUID = 1L;
    private List<User> userList = new ArrayList<>();

    /**
     * this writes the Databases Objects to the database.dat file
     * @param db the Object that is going to be fed to the database.dat 
     */
    public static void writeDatabase(Database db) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
        oos.writeObject(db);
    }

    /**
     * reads the data that was serialized from the last time the program was launched
     * @return database object with all the user infor that was previously seriolized
     */
    public static Database readDatabase() throws IOException, ClassNotFoundException {
        //to check if directory exists first, if not, a dir named "data" will be created
        File storageLocation = new File(storeDir);
        if(!storageLocation.exists()) {
            storageLocation.mkdirs();
        }

        //next part is the .dat file itself
        storageLocation = new File(storeDir + File.separator + storeFile);
        storageLocation.createNewFile(); //create the file, if it doesn't already exist
        FileInputStream fis = new FileInputStream(storeDir + File.separator + storeFile);

        if (fis.available() == 0) {
            //nothing to read
            return Database.generateDB();
        }

        ObjectInputStream ois = new ObjectInputStream(fis);
        Database db = (Database) ois.readObject();
        return db;
    }

    /**
     * creates a DB file that has the user called stock with their album info
     * @return the DB with the stock user infor is returned
     */
    private static Database generateDB() throws IOException {
        //creates a new DB file, populated with a stock user, and saves it
        Database freshDB = new Database();
        User stockUser = new User("stock", "stock");
        Album stockAlbum = new Album("Stock Photos");

        File stock1 = new File("data/stock/stock1.jpeg");
        File stock2 = new File("data/stock/stock2.jpeg");
        File stock3 = new File("data/stock/stock3.jpeg");
        File stock4 = new File("data/stock/stock4.jpeg");
        File stock5 = new File("data/stock/stock5.jpeg");
        stockAlbum.addphoto(stock1.getAbsolutePath(), "Elongated cat", "length=long,animal=cat");
        stockAlbum.addphoto(stock2.getAbsolutePath(), "Face-Swapped cat", "style=faceswap,animal=cat");
        stockAlbum.addphoto(stock3.getAbsolutePath(), "Dog can't figure out computer science", "emotion=sad,animal=dog");
        stockAlbum.addphoto(stock4.getAbsolutePath(), "Cat just handed in a computer science project", "expression=smiling,animal=cat");
        stockAlbum.addphoto(stock5.getAbsolutePath(), "Dog got the max score on his 'Photo Library Project'", "expression=smiling,animal=dog");

        stockUser.addAlbum(stockAlbum);
        freshDB.addUser(stockUser);

        Database.writeDatabase(freshDB);

        return freshDB;
    }

    /**
     * added a new user with their password to Databse object
     * @param username the new username to be added
     * @param password new password to be added
     */
    public void addUser(String username, String password) {
        User newUser = new User(username, password);
        this.userList.add(newUser);
    }

    /**
     * adds a username to the database
     * @param newUser username to be added to the database
     */
    public void addUser(User newUser) {
        this.userList.add(newUser);
    }

    /**
     * deletes a user from the database
     * @param username the user to be deleted username
     */
    public void deleteUser(String username) {
        for(User existingUser : this.userList) {
            if(existingUser.getID().equals(username)) {
                this.userList.remove(existingUser);
                return;
            }
        }
    }

    /**
     * varifies the username and password passed is a valid user
     * @param username the user to be check
     * @param password the password to be checked
     * @return boolean stating true if the username and password is of a vaild users
     */
    public boolean authenticate(String username, String password) {
        for(User existingUser : this.userList) {
            if(existingUser.getID().equals(username) && existingUser.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }


    /**
     * gives the User Object of the username given
     * @param username the username of the User to be returned
     * @return g User Object of the username argument given
     */
    public User getUser(String username) {
        for(User existingUser : this.userList) {
            if(existingUser.getID().equals(username)) {
                return existingUser;
            }
        }

        return null;
    }

    /** 
     * check the users in the databse and if the name matches the previous object is switched with the newuser
     * @param newUser the newuser obj to replace the previous one
     */
    public void replaceUser(User newUser) throws IOException {
        for(int i = 0; i < this.userList.size(); i++) {
            if(this.userList.get(i).getID().equals(newUser.getID())) {
                this.userList.set(i, newUser);
                break;
            }
        }
        Database.writeDatabase(this);
    }

    /**
     * a list of all the users in the databse is given
     * @return a list<User> that were previously serialized
     */
    public List<User> getUserList() {
        return this.userList;
    }
}