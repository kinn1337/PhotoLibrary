package photos.view;

import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.List;
import album.User;

/**
 * creates the admins User screen
 * where they can create new users or delete new users
 * @author Michael cyriac
 * @autho Russell NG
 */
public class adminSubsystemController extends parentController{
    @FXML ListView<String> userList;
    @FXML TextField newUserID;
    @FXML TextField newUserPassword;
    /**
     * sets vist to show all user and their passwors
     */
    @FXML
    public void initialize() {
        userList.setItems(userListObservable);
        loadUserList();

        newUserPassword.setOnKeyPressed(e -> {
            if(e.getCode() == (KeyCode.ENTER)) {
                addUser();
            }
        });
    }

    private ObservableList<String> userListObservable = FXCollections.observableArrayList();

    /**
     * sets the Observable list with each users username and password
     */
    private void loadUserList() {
        //populates observable list using database to connect with list view
        Database db = super.getDB();
        List<User> existingUsersList = db.getUserList();
        userListObservable.clear();
        for(User existingUser : existingUsersList) {
            userListObservable.add("Username: " + existingUser.getID() + "\nPassword: " + existingUser.getPassword());
        }
        userList.getSelectionModel().selectFirst();
    }

    /**
     * adds a newUser to the database where their can be used as a valid login
     */
    public void addUser() {
        //to add a user to database
        if(newUserID.getText().isEmpty()) {
            showError("Missing username!", "Attempting to add a user without a username, please add a username.");
            return;
        }

        if(newUserPassword.getText().isEmpty()) {
            showError("Missing password!", "Attempting to add a user without a password, please add a password.");
            return;
        }

        if(newUserID.getText().equalsIgnoreCase("admin") || newUserID.getText().equalsIgnoreCase("stock")) {
            showError("Invalid username!", newUserID.getText() + " is a system username and can not be used.");
            return;
        }

        Database db = super.getDB();
        List<User> existingUsersList = db.getUserList();
        for (User existingUser : existingUsersList) {
            if (existingUser.getID().equalsIgnoreCase(newUserID.getText())) {
                showError("Username taken!", "The username you are trying to add already exists. Please use a different one.");
                return;
            }
        }

        db.addUser(newUserID.getText(), newUserPassword.getText());
        try {
            super.saveDB(db);
        }
        catch (IOException e) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("Save Error");
            popup.setHeaderText("Failed to write to database file ending in .dat!");
            popup.setContentText("The .dat file could not be found, make sure a 'data' directory exists under 'photos80'," +
                    "\nthen restart this program.");
            popup.showAndWait();
            System.exit(1);
        }
        loadUserList();
        userList.getSelectionModel().selectLast();
    }

    /**
     * delets the current selected user
     */
    public void deleteUser() {
        //delete user from database
        if(userList.getSelectionModel().isEmpty()) {
            return;
        }
        Database db = super.getDB();
        int selectedInd = userList.getSelectionModel().getSelectedIndex();
        String[] selectedUser = userListObservable.get(selectedInd).split(", ");
        db.deleteUser(selectedUser[0]);
        try {
            super.saveDB(db);
        }
        catch (IOException e) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("Save Error");
            popup.setHeaderText("Failed to write to database file ending in .dat!");
            popup.setContentText("The .dat file could not be found, make sure a 'data' directory exists under 'photos80'," +
                    "\nthen restart this program.");
            popup.showAndWait();
            System.exit(1);
        }
        loadUserList();
    }

    /**
     * shows a wrror message popup
     * @param header the header of the message
     * @param message the message to showup in the message
     */
    private void showError(String header, String message) {
        //to show an error popup
        Alert popup = new Alert(Alert.AlertType.ERROR);
        popup.initOwner(super.mainStage);
        popup.setTitle("Error");
        popup.setHeaderText(header);
        popup.setContentText(message);
        popup.showAndWait();
    }
}