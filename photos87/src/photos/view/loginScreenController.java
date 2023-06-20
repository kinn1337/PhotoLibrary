package photos.view;

import album.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * shows a login scres where the user can login to see their user page
 * as either a regular user, admin or stock which has preselected stock photos
 * @author Michael cyriac
 * @autho Russell NG
 */
public class loginScreenController extends parentController implements objectTracker{
    @FXML private TextField userID;
    @FXML private PasswordField userPassword;
    @FXML private Button loginButton;
    /**
     * allowes the user to click the enter key or click the login button to login
     */
    @FXML
    public void initialize() {
        userPassword.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
                authenticate();
            }
        });

        loginButton.setOnAction(e -> {
            super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
            authenticate();
        });
    }

    @Override
    public void start(List<Object> objectList) {
        //to be able to use parent switchScene
        //the T extends objectTracker variant is needed for keeping track of the logged in user
        return;
    }

    /**
     * checks the databse to verafy if the user is a valid user
     */
    public void authenticate() {
        if(userID.getText().equals("admin") && userPassword.getText().equals("admin")) {
            //admin login
            super.switchScene("./adminSubsystem.fxml", "Admin System");
        }
        else {
            //regular user login
            //passes to userLookup method, which check if user/password combination exists
            Boolean validLogin = super.getDB().authenticate(userID.getText(), userPassword.getText());
            if(validLogin) {
                List<Object> bundledObjects = new ArrayList<>();
                bundledObjects.add(super.getDB().getUser(userID.getText()));
                super.switchScene(bundledObjects, "./albumOverview.fxml", "Albums");
            }
            else {
                showError("Invalid login!",
                        "Username and Password combination not found.\n" +
                                "Try again, or use the stock account. (Default username and password: stock)");
            }
        }
    }


    /**
     * shows a wrror message popup
     * @param header the header of the message
     * @param message message the message to showup in the message
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