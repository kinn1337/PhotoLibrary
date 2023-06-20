package photos.view;

import Database.Database;
import album.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * creates function that all controlers will inherate since they all share a few similar functions
 * @author Michael cyriac
 * @autho Russell NG
 */
public class parentController {
    //parent controller class to create general methods used by all controllers
    public Stage mainStage;

    /**
     * allowes the user to exit the program without causing errors
     */
    public void exitProgram() {
        System.exit(0);
    }

    /**
     * when the method is classed sents the user back to the login screen
     * @param e the actioneven that triggeres the call of this method
     */
    public void logout(ActionEvent e) {
        //logs out of account, returning to login screen
        this.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
        switchScene("./loginScreen.fxml", "Photos");
    }

    /**
     * dreas the databse for previously saves data
     * @return the databse that has all the user information
     */
    public Database getDB() {
        Database loadedDB;
        try {
            //read database and returns it
            loadedDB = Database.readDatabase();
        }
        catch (IOException ioe) {
            loadedDB = null;
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("Data Load Error");
            popup.setHeaderText("Data files failed to load!");
            popup.setContentText("Either the .dat file could not be found or some stock photos are missing from the directory." +
                    "\nMake sure a 'data' and 'stock' directory exists under 'photos80'," +
                    "\nand make sure that stock1-stock5.jpeg are under 'stock'.");
            popup.showAndWait();
            System.exit(1);
        }
        catch (ClassNotFoundException cnfe) {
            loadedDB = null;
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("Load Error");
            popup.setHeaderText("Database file corrupted!");
            popup.setContentText("The .dat file contains a class that could not be loaded." +
                    "\nTo fix this, please delete the 'database.dat' file under the 'data' directory," +
                    "\nand then run the program again.");
            popup.showAndWait();
            System.exit(1);
        }

        return loadedDB;
    }

    /**
     * serializes the information so that when the user returns the informaiton is not lost
     * @param dataB the data to be saved
     */
    public void saveDB(Database dataB) throws IOException {
        //save database changes
        Database.writeDatabase(dataB);
    }

    /**
     * whitches scenes to the scene specified
     * @param fxmlFile the new scene to changed to 
     * @param title the title of the scene
     */
    public void switchScene(String fxmlFile, String title) {
        //switching to a different scene by loading a different fxml file
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile)); //target login screen fxml
            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setTitle(title);
            mainStage.setScene(scene);
            mainStage.show(); //display new scene
        } catch (Exception e) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("FXML Load Error");
            popup.setHeaderText("Failed to load FXML file!");
            popup.setContentText("An .fxml file could not be found, so the next screen could not be loaded.\nPlease reinstall the program.");
            popup.showAndWait();
            System.exit(1);
        }
    }

    /**
     * switches the scnee to the new specified scene while also alloweing the user
     * the send information between scenes
     * @param <T>
     * @param objectList the list of information to be sent to another scene
     * @param fxmlFile the new scene to changed to 
     * @param title the title of the scene
     */
    public <T extends objectTracker> void switchScene(List<Object> objectList, String fxmlFile, String title) {
        //for switching scenes and passing the current user object
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile)); //target login screen fxml
            Parent root = loader.load();
            T nextController = loader.getController();
            nextController.start(objectList);
            Scene scene = new Scene(root);
            mainStage.setTitle(title);
            mainStage.setScene(scene);
            mainStage.show(); //display new scene
        } catch (Exception exc) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("FXML Load Error");
            popup.setHeaderText("Failed to load FXML file!");
            popup.setContentText("An .fxml file could not be found, so the next screen could not be loaded.\nPlease reinstall the program.");
            popup.showAndWait();
            System.exit(1);
        }
    }
}