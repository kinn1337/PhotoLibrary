package photos.view;

import Database.Database;
import album.Album;
import album.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * shows all the albums that are owned by this perticular user
 * while also permitting them to delete, create or even rename a album
 * @author Michael cyriac
 * @autho Russell NG
 */
public class albumOverviewController extends parentController implements objectTracker{
    @FXML ListView<String> albumList;
    @FXML Button createButton;
    @FXML Button deleteButton;
    @FXML Button openAlbumButton;
    @FXML Button searchButton;
    @FXML AnchorPane createAlbumPane;
    @FXML TextField newAlbumName;
    @FXML Button confirmCreateButton;
    @FXML Button cancelCreateButton;
    @FXML Button renameButton;
    /**
     * sets up the buttons in the scene to swtich scenes
     * create delete and add albums
     */
    public void initialize() {
        searchButton.setOnAction(e -> {
            super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
            List<Object> bundledObjects = new ArrayList<>();
            bundledObjects.add(this.current_user);
            super.switchScene(bundledObjects, "./search.fxml", "Search");
        });

        createButton.setOnAction(e -> {
            createAlbumPane.setVisible(true);
            newAlbumName.requestFocus();
        });

        deleteButton.setOnAction(e -> {
            if(albumInfo.isEmpty()) {
                return;
            }

            int targetInd = albumList.getSelectionModel().getSelectedIndex();
            this.current_user.removeAlbum(targetInd);
            Database updatedDB = super.getDB();
            try {
                updatedDB.replaceUser(this.current_user);
            }
            catch (IOException ioe) {
                Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(this.mainStage);
                popup.setTitle("Save Error");
                popup.setHeaderText("Failed to save to .dat file!");
                popup.setContentText("The .dat file is broken or missing, please make sure a 'data' directory exists under 'photos80'," +
                        "\nand if a .dat file exists, please delete that file and restart the program.");
                popup.showAndWait();
                System.exit(1);
            }
            loadAlbums(this.current_user.getAlbums());
        });

        newAlbumName.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                createAlbum();
            }

            if(e.getCode() == KeyCode.ESCAPE) {
                newAlbumName.clear();
                createAlbumPane.setVisible(false);
                albumList.requestFocus();
            }
        });

        confirmCreateButton.setOnAction(e -> {
            createAlbum();
        });

        cancelCreateButton.setOnAction(e -> {
            newAlbumName.clear();
            createAlbumPane.setVisible(false);
        });
        
        renameButton.setOnAction(e->{
        	int selectedindex = albumList.getSelectionModel().getSelectedIndex();
        	
        	TextInputDialog dialog = new TextInputDialog();
        	dialog.setHeaderText("Selected album : "+ this.current_user.getAlbums().get(selectedindex).getAlbumName());
            dialog.setContentText("Please enter the new album name:");
        	// the string from dialog box is in result
        	Optional<String> result = dialog.showAndWait();
        	if(result.isPresent()) {
        		this.current_user.getAlbums().get(selectedindex).editAlbumName(result.get());
        		Database updatedDB = super.getDB();
                try {
                    updatedDB.replaceUser(this.current_user);
                }
                catch (IOException ioe) {
                    Alert popup = new Alert(Alert.AlertType.ERROR);
                    popup.initOwner(this.mainStage);
                    popup.setTitle("Save Error");
                    popup.setHeaderText("Failed to save to .dat file!");
                    popup.setContentText("The .dat file is broken or missing, please make sure a 'data' directory exists under 'photos80'," +
                            "\nand if a .dat file exists, please delete that file and restart the program.");
                    popup.showAndWait();
                    System.exit(1);
                }
                loadAlbums(this.current_user.getAlbums());
        	}
        	
        });

        openAlbumButton.setOnAction(e -> {
            if(albumList.getSelectionModel().isEmpty()) {
                //no albums exist, so can't open any
                return;
            }
            super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
            int selectedInd = albumList.getSelectionModel().getSelectedIndex();
            Album selectedAlbum = this.current_user.getAlbums().get(selectedInd);
            List<Object> bundledObjects = new ArrayList<>();
            bundledObjects.add(this.current_user);
            bundledObjects.add(selectedAlbum);
            super.switchScene(bundledObjects, "./openedAlbum.fxml", selectedAlbum.getAlbumName().toUpperCase(Locale.ROOT));
        });
    }

    private User current_user;
    private ObservableList<String> albumInfo = FXCollections.observableArrayList();

    @Override
    public void start(List<Object> objectList) {
        //load albums according to the currentUser
        this.current_user = (User)objectList.get(0);
        loadAlbums(this.current_user.getAlbums());
    }

    /**
     * sets the Observable list up with the album name # photos and date rage of the photos in the album
     * @param albumsList the list array that is set with the Observable list being createdd
     */
    private void loadAlbums(List<Album> albumsList) {
        if(albumsList != null) {
            albumInfo.clear();
            for (Album existingAlbum : albumsList) {
                albumInfo.add("Name: " + existingAlbum.getAlbumName() +
                        "\nNumber of photos: " + existingAlbum.numOfPhotos() +
                        "\nDate range: " + existingAlbum.dateRange());
            }
        }
        albumList.setItems(albumInfo);
        albumList.getSelectionModel().selectFirst();
    }

    /**
     * function that creates a new album and inserts it into the database
     */
    private void createAlbum() {
        if(newAlbumName.getText().isEmpty()) {
            this.showError("Album name empty!", "Please input an album name.");
            return;
        }

        Album newAlbum = new Album(newAlbumName.getText());
        if(!this.current_user.albumExists(newAlbumName.getText())) {
            this.current_user.addAlbum(newAlbum);
            Database updatedDB = super.getDB();
            try {
                updatedDB.replaceUser(this.current_user);
            }
            catch (IOException ioe) {
                Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(this.mainStage);
                popup.setTitle("Save Error");
                popup.setHeaderText("Failed to save to .dat file!");
                popup.setContentText("The .dat file is broken or missing, please make sure a 'data' directory exists under 'photos80'," +
                        "\nand if a .dat file exists, please delete that file and restart the program.");
                popup.showAndWait();
                System.exit(1);
            }
            loadAlbums(this.current_user.getAlbums());
            newAlbumName.clear();
            createAlbumPane.setVisible(false);
            albumList.getSelectionModel().selectLast();
            albumList.requestFocus();
        }
        else {
            this.showError("Album name taken!", "You already have an album of the same name, please try another name");
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