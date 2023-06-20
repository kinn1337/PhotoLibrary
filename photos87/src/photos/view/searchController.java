package photos.view;

import Database.Database;
import album.Album;
import album.Photo;
import album.Tag;
import album.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * alloes the user to search the users albums for photos
 * that have 2 tags such that the photo has tag1 and tag2 or the photo has Tag1 OR tag2
 * and they can create an new album with the new search results gained from the search
 * @author Michael cyriac
 * @autho Russell NG
 */
public class searchController extends parentController implements objectTracker{
    @FXML private Button returnButton;
    @FXML private Button searchButton;
    @FXML private Button createAlbumButton;
    @FXML private ChoiceBox<String> searchBy;
    @FXML private TextField searchBar;
    @FXML private ListView<Photo> imageListView;
    @FXML
    public void initialize() {
        returnButton.setOnAction(e -> {
            super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
            List<Object> bundledObjects = new ArrayList<>();
            bundledObjects.add(this.current_user);
            super.switchScene(bundledObjects, "./albumOverview.fxml", "Albums");
        });

        searchBy.setItems(FXCollections.observableArrayList("Date", "Tag"));
        searchingByDate();

        searchBy.setOnAction(e -> {
            if(searchBy.getSelectionModel().getSelectedItem().equals("Date")) {
                searchingByDate();
            }
            if(searchBy.getSelectionModel().getSelectedItem().equals("Tag")) {
                searchingByTag();
            }
        });

        searchBar.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                loadImages();
            }
        });

        searchButton.setOnAction(e -> {
            loadImages();
        });

        createAlbumButton.setOnAction(e -> {
            createAlbum();
        });
    }

    private User current_user;
    private ObservableList<Photo> photosOBSList = FXCollections.observableArrayList();

    @Override
    public void start(List<Object> objectList) {
        this.current_user = (User)objectList.get(0);
    }

    /**
     * sets up the prompt to show the format for th date search
     */
    private void searchingByDate() {
        searchBy.getSelectionModel().selectFirst();
        searchBar.setPromptText("MM/dd/yyyy-MM/dd/yyyy (ex: 04/01/2001-11/11/2011)");
    }

    /**
     * sets the prompt to show the format for searching tags without date values
     */
    private void searchingByTag() {
        searchBy.getSelectionModel().selectLast();
        searchBar.setPromptText("tagName=tagValue, tagName=tagValue AND tagName=tagValue, tagName=tagValue OR tagName=tagValue");
    }

    /**
     * does the actual search by date for the photos the user has
     */
    private void dateSearch() {
        String inputDateRange = searchBar.getText();
        if(inputDateRange.split("-").length != 2) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(super.mainStage);
            popup.setTitle("Bad Input");
            popup.setHeaderText("Search format invalid!");
            popup.setContentText("There was an error in the way you inputted your search." +
                    "\nThe format should be first day to last day in the format: 'MM/dd/yyyy-MM/dd/yyyy'" +
                    "\n(MONTH/DAY/YEAR-MONTH/DAY/YEAR)");
            popup.showAndWait();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar earliestDay = Calendar.getInstance();
        Calendar latestDay = Calendar.getInstance();

        try {
            earliestDay.setTime(sdf.parse(inputDateRange.split("-")[0]));
            latestDay.setTime(sdf.parse(inputDateRange.split("-")[1]));
        }
        catch (ParseException pe) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(super.mainStage);
            popup.setTitle("Bad Input");
            popup.setHeaderText("Search format invalid!");
            popup.setContentText("There was an error in the way you inputted your search." +
                    "\nThe format should be first day to last day in the format: 'MM/dd/yyyy-MM/dd/yyyy'" +
                    "\n(MONTH/DAY/YEAR-MONTH/DAY/YEAR)");
            popup.showAndWait();
            return;
        }
        earliestDay.set(Calendar.HOUR_OF_DAY, 0);
        earliestDay.set(Calendar.MINUTE, 0);
        earliestDay.set(Calendar.MILLISECOND, 0);
        latestDay.set(Calendar.HOUR_OF_DAY, 23);
        latestDay.set(Calendar.MINUTE, 59);
        latestDay.set(Calendar.MILLISECOND, 59);
        photosOBSList.clear();
        for(Album existingAlbum : this.current_user.getAlbums()) {
            for(Photo existingPhoto : existingAlbum.getPhotos()) {
                if(!photosOBSList.contains(existingPhoto)) {
                    if (existingPhoto.getDate().compareTo(earliestDay) >= 0 && existingPhoto.getDate().compareTo(latestDay) <= 0) {
                        photosOBSList.add(existingPhoto);
                    }
                }
            }
        }
    }

    /**
     * does the search by  tag for the photos by the user
     */
    private void tagSearch() {
        String tagInput = searchBar.getText().strip();
        int equalCount = (int)tagInput.chars().filter(ch -> ch == '=').count();
        if(equalCount != 1 && equalCount != 2) {
            //error
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(super.mainStage);
            popup.setTitle("Bad Input");
            popup.setHeaderText("Search format invalid!");
            popup.setContentText("There was an error in the way you inputted your search." +
                    "\nThe format should be 'tagName=tagValue' for single tag-value searches." +
                    "\nIf you want to do a conjuction/disjunction, the format should be:" +
                    "\n'tagName=tagValue AND tagName=tagValue' or 'tagName=tagValue OR tagName=tagValue'");
            popup.showAndWait();
            return;
        }

        if(equalCount == 1 && (!tagInput.contains("AND") && !tagInput.contains("OR"))) {
            //single tag
            Tag searchTag = new Tag(tagInput);
            photosOBSList.clear();
            for(Album existingAlbum : this.current_user.getAlbums()) {
                for(Photo existingPhoto : existingAlbum.getPhotos()) {
                    if(!photosOBSList.contains(existingPhoto)) {
                        for(Tag existingTag : existingPhoto.getTags()) {
                            if(existingTag.equalsTag(searchTag)) {
                                photosOBSList.add(existingPhoto);
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if (equalCount == 2 && (tagInput.contains("AND") && !tagInput.contains(("OR")))){
            //conjuction
            if(tagInput.split("AND")[0].split("=").length != 2 ||
                    tagInput.split("AND")[1].split("=").length != 2) {
                //error
                Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(super.mainStage);
                popup.setTitle("Bad Input");
                popup.setHeaderText("Search format invalid!");
                popup.setContentText("There was an error in the way you inputted your search." +
                        "\nThe format should be 'tagName=tagValue' for single tag-value searches." +
                        "\nIf you want to do a conjuction/disjunction, the format should be:" +
                        "\n'tagName=tagValue AND tagName=tagValue' or 'tagName=tagValue OR tagName=tagValue'");
                popup.showAndWait();
                return;
            }
            Tag searchTag1 = new Tag(tagInput.split("AND")[0].strip());
            Tag searchTag2 = new Tag(tagInput.split("AND")[1].strip());
            photosOBSList.clear();
            for(Album existingAlbum : this.current_user.getAlbums()) {
                for(Photo existingPhoto : existingAlbum.getPhotos()) {
                    if(!photosOBSList.contains(existingPhoto)) {
                        boolean tag1Exists = false;
                        boolean tag2Exists = false;
                        for(Tag existingTag : existingPhoto.getTags()) {
                            if(tag1Exists && tag2Exists) {
                                break;
                            }
                            if(existingTag.equalsTag(searchTag1)) {
                                tag1Exists = true;
                            }
                            if(existingTag.equalsTag(searchTag2)) {
                                tag2Exists = true;
                            }
                        }
                        if(tag1Exists && tag2Exists) {
                            photosOBSList.add(existingPhoto);
                        }
                    }
                }
            }
        }
        else if (equalCount == 2 && (!tagInput.contains("AND") && tagInput.contains(("OR")))) {
            //disjunction
            if(tagInput.split("OR")[0].split("=").length != 2 ||
                    tagInput.split("OR")[1].split("=").length != 2) {
                //error
                Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(super.mainStage);
                popup.setTitle("Bad Input");
                popup.setHeaderText("Search format invalid!");
                popup.setContentText("There was an error in the way you inputted your search." +
                        "\nThe format should be 'tagName=tagValue' for single tag-value searches." +
                        "\nIf you want to do a conjuction/disjunction, the format should be:" +
                        "\n'tagName=tagValue AND tagName=tagValue' or 'tagName=tagValue OR tagName=tagValue'");
                popup.showAndWait();
                return;
            }
            Tag searchTag1 = new Tag(tagInput.split("OR")[0].strip());
            Tag searchTag2 = new Tag(tagInput.split("OR")[1].strip());
            photosOBSList.clear();
            for(Album existingAlbum : this.current_user.getAlbums()) {
                for(Photo existingPhoto : existingAlbum.getPhotos()) {
                    if(!photosOBSList.contains(existingPhoto)) {
                        for(Tag existingTag : existingPhoto.getTags()) {
                            if(existingTag.equalsTag(searchTag1) || existingTag.equalsTag(searchTag2)) {
                                photosOBSList.add(existingPhoto);
                                break;
                            }
                        }
                    }
                }
            }
        }
        else {
            //error
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(super.mainStage);
            popup.setTitle("Bad Input");
            popup.setHeaderText("Search format invalid!");
            popup.setContentText("There was an error in the way you inputted your search." +
                    "\nThe format should be 'tagName=tagValue' for single tag-value searches." +
                    "\nIf you want to do a conjuction/disjunction, the format should be:" +
                    "\n'tagName=tagValue AND tagName=tagValue' or 'tagName=tagValue OR tagName=tagValue'");
            popup.showAndWait();
        }
    }

    /**
     * load the images so that they are showed with a thumbnail
     * and caption
     */
    private void loadImages() {
        if(searchBy.getSelectionModel().getSelectedItem().equals("Date")) {
            //searching by date
            dateSearch();
        }
        else if(searchBy.getSelectionModel().getSelectedItem().equals("Tag")) {
            //searching by tag
            tagSearch();
        }
        imageListView.setItems(photosOBSList);
        imageListView.setCellFactory(lv -> new ListCell<Photo>() {
            private ImageView imageDisplay = new ImageView();
            @Override
            public void updateItem(Photo photoOBJ, boolean empty) {
                imageDisplay.setPreserveRatio(true);
                imageDisplay.setSmooth(false);
                imageDisplay.setFitHeight(64);
                imageDisplay.setFitWidth(64);
                super.updateItem(photoOBJ, empty);
                if(empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        imageDisplay.setImage(photoOBJ.getImage());
                    }
                    catch (IOException ioe) {
                        imageDisplay.setImage(null);
                    }
                    setText(photoOBJ.getCap());
                    setGraphic(imageDisplay);
                }
            }
        });
        imageListView.setFixedCellSize(96);

        imageListView.getSelectionModel().select(0);
    }

    /**
     * takes all the photos currently being displayed by photos and creates an album using them
     */
    private void createAlbum() {
        if(this.imageListView.getSelectionModel().isEmpty()) {
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(super.mainStage);
            popup.setTitle("Create Album Error");
            popup.setHeaderText("No photos!");
            popup.setContentText("There are no search results to add to a new album.");
            popup.showAndWait();
            this.imageListView.requestFocus();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Create album with these search results?");
        dialog.setContentText("Please enter the name of the new album: ");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            if(this.current_user.albumExists(result.get())) {
                Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(super.mainStage);
                popup.setTitle("Create Album Error");
                popup.setHeaderText("Album already exists!");
                popup.setContentText("The album you tried to create already exists!" +
                        "\nEither rename the old album, delete the old album, or pick a different name," +
                        "then try again.");
                popup.showAndWait();
                this.imageListView.requestFocus();
                return;
            }

            Album newAlbum = new Album(result.get());
            for(Photo existingPhoto : this.photosOBSList) {
                newAlbum.addObjPhoto(existingPhoto);
            }
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
        }
        this.imageListView.requestFocus();
    }
}