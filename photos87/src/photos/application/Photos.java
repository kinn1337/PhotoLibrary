//CODE BY RUSSELL NG
//AND MICHAEL CYRIAC
package photos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * this launches the photos program to the login screen
 * @author Michael cyriac
 * @autho Russell NG
 *
 */
public class Photos extends Application{
    @Override
    public void start(Stage mainStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/loginScreen.fxml")); //target login screen fxml
            Parent rootLogin = loader.load(); //load login screen
            Scene sceneLogin = new Scene(rootLogin); //attach login screen to scene
            mainStage.setTitle("Photos");
            mainStage.setScene(sceneLogin);
            mainStage.show(); //display scene
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * launchec the program at the login screen
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

}