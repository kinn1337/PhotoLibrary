package photos.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import Database.Database;
import album.Album;
import album.Photo;
import album.Tag;
import album.User;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * this is the controler for the OpenedAlbum.fxml
 * that presentes the user with a tableview that
 * looks like a list view
 * showing the user all the thubnail of the photos of that album along its caption
 * when  perticual image is chose the image is displayes on the side
 * with its caption, date of last edit and tags
 * the user is also given the option to
 * view images in slideshow format, edit cation or tags,
 * add,delete, copy or move an image
 * @author Michael cyriac
 * @autho Russell NG
 *
 */
public class openedAlbumController extends parentController implements objectTracker{
	
	@FXML private ImageView imgbox;
	@FXML private TextArea caption_text;
	@FXML private TextArea Tags_text;
	@FXML private Button returnButton;
	@FXML private Button addImageButton;
	@FXML private Label photo_Date;
	@FXML private Label albumTitleLabel;
	@FXML private ListView<Photo> imageListView;
    @FXML private Button viewButton;
    @FXML private Button editButton;
    @FXML private Button moveButton;
    @FXML private Button delet_button;
    @FXML private Button copyButton;
	private User current_user;
	private Album curr_albm;
	private ObservableList<Photo> photosOBSList;

	//list container in GUI
     
	/**
	 * initializes this stage
	 * and give the user the ability to click buttons to 
	 * either edit, copy, add, and move an image
	 */
	public void initialize() {
        returnButton.setOnAction(e -> {
            super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
			List<Object> bundledObjects = new ArrayList<>();
			bundledObjects.add(this.current_user);
            super.switchScene(bundledObjects,"./albumOverview.fxml", "Albums");
        });

        //shanges from this scene to the slideshow scene
        viewButton.setOnAction(e ->{
        	if(curr_albm.getPhotos().isEmpty()) { // no images to slide show
        		//show popup stating their are no images to be in slideshow
        		Alert popup = new Alert(Alert.AlertType.ERROR);
                popup.initOwner(super.mainStage);
                popup.setTitle("Error");
                popup.setHeaderText("No photos in album!");
                popup.setContentText("There are no photos in this album to be displayed in View.");
                popup.showAndWait();
        	} else {
        		super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
        		List<Object> bundledObjects = new ArrayList<>();
        		bundledObjects.add(this.current_user);
        		bundledObjects.add(this.curr_albm);
        		if(imageListView.getSelectionModel().isEmpty()) //nothing was selected so we start show with index 0
        			bundledObjects.add(0);
        		else {
        			bundledObjects.add(imageListView.getSelectionModel().getSelectedIndex());
        		}
        		super.switchScene(bundledObjects, "./photoView.fxml", "Slide Show");
        }
        });
        
        // button to edit the caption and tags
        editButton.setOnAction(e->{
			if(imageListView.getSelectionModel().isEmpty()) {
				return;
			}

        	int selectIndex = imageListView.getSelectionModel().getSelectedIndex();
        	this.curr_albm.getPhotos().get(selectIndex).editCaption(caption_text.getText());
        	this.curr_albm.getPhotos().get(selectIndex).newtags(Tags_text.getText());
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
			loadImages();
			imageListView.getSelectionModel().select(selectIndex);
        });
        
        //button to move a image from the current album to a new abum
        moveButton.setOnAction(e->{
			if(imageListView.getSelectionModel().isEmpty()) {
				return;
			}

        	Photo selectedPhoto = imageListView.getSelectionModel().getSelectedItem();
        	TextInputDialog dialog = new TextInputDialog();
        	dialog.setContentText("Enter the name of the album you want to move the selected image to: ");
        	dialog.setHeaderText("Selected image with caption '"+ selectedPhoto.getCap() + "'");
        	// the string from dialog box is in result
        	Optional<String> result = dialog.showAndWait();
        	if(result.isPresent()) {
        		//if album to move is not owned by this user
        		if(!current_user.albumExists(result.get())) {
        			Alert popup = new Alert(AlertType.ERROR);
        	    	popup.initOwner(mainStage);
        	    	popup.setTitle("Error");
        	    	popup.setHeaderText("Album not found!");
        	    	popup.setContentText("The album you are trying to access does not exist.");
        	    	popup.showAndWait();
        		}else {
        			//get the index of the album they wanna add to
        			Album targetAlbum = current_user.getAlbum(result.get());
					if(targetAlbum.getAlbumName().equals(this.curr_albm.getAlbumName())) {
						Alert popup = new Alert(AlertType.ERROR);
						popup.initOwner(mainStage);
						popup.setTitle("Error");
						popup.setHeaderText("Invalid move!");
						popup.setContentText("Attempt to move a photo from the current album into the same album.");
						popup.showAndWait();
						return;
					}
        			//add to the new album
					if(!targetAlbum.photoExists(selectedPhoto)) {
						targetAlbum.addObjPhoto(selectedPhoto);
					}
        			//delet from the current album
        			this.curr_albm.removePhoto(selectedPhoto);
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
					loadImages();
        		}
        	}
        });
        
        //delete the selected photo
        delet_button.setOnAction(e ->{
			if(imageListView.getSelectionModel().isEmpty()) {
				return;
			}

        	int selectIndex = imageListView.getSelectionModel().getSelectedIndex();
			this.curr_albm.getPhotos().remove(selectIndex);
        	Database updatedDB = super.getDB();
			try {
				updatedDB.replaceUser(this.current_user);
			}
			catch(IOException ioe) {
				Alert popup = new Alert(Alert.AlertType.ERROR);
				popup.initOwner(this.mainStage);
				popup.setTitle("Save Error");
				popup.setHeaderText("Failed to save to .dat file!");
				popup.setContentText("The .dat file is broken or missing, please make sure a 'data' directory exists under 'photos80'," +
						"\nand if a .dat file exists, please delete that file and restart the program.");
				popup.showAndWait();
				System.exit(1);
			}
			loadImages();
        });
        //copy an image
        copyButton.setOnAction(e ->{
			if(imageListView.getSelectionModel().isEmpty()) {
				return;
			}

			Photo selectedPhoto = imageListView.getSelectionModel().getSelectedItem();
			TextInputDialog dialog = new TextInputDialog();
			dialog.setContentText("Enter the name of the album you want to copy the selected image to: ");
			dialog.setHeaderText("Selected image with caption '"+ selectedPhoto.getCap() + "'");
			// the string from dialog box is in result
			Optional<String> result = dialog.showAndWait();
			if(result.isPresent()) {
				//if album to move is not owned by this user
				if (!current_user.albumExists(result.get())) {
					Alert popup = new Alert(AlertType.ERROR);
					popup.initOwner(mainStage);
					popup.setTitle("Error");
					popup.setHeaderText("Album not found!");
					popup.setContentText("The album you are trying to access does not exist.");
					popup.showAndWait();
				} else {
					//get the index of the album they wanna add to
					Album targetAlbum = current_user.getAlbum(result.get());
					if(!targetAlbum.photoExists(selectedPhoto)) {
						targetAlbum.addObjPhoto(selectedPhoto);
					}
					else {
						Alert popup = new Alert(AlertType.ERROR);
						popup.initOwner(mainStage);
						popup.setTitle("Warning");
						popup.setHeaderText("Photo already exists!");
						popup.setContentText("This photo is already in the album you are trying to copy to.");
						popup.showAndWait();
						return;
					}
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
					loadImages();
					imageListView.getSelectionModel().select(selectedPhoto);
				}
			}
        });

		addImageButton.setOnAction(e -> {
			super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
			List<Object> bundledObjects = new ArrayList<>();
			bundledObjects.add(this.current_user);
			bundledObjects.add(this.curr_albm);
			super.switchScene(bundledObjects,"./AddPhoto.fxml", "Add Photo");
		});

		imageListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
			displaySelectedImage();
		});
    }
	
	
	public void start(List<Object> objectList) {
		//load albums according to the currentUser
		this.current_user = (User)objectList.get(0);
		this.curr_albm = (Album)objectList.get(1);
		albumTitleLabel.setText(this.curr_albm.getAlbumName().toUpperCase(Locale.ROOT));
		loadImages();
	}

	/**
	 * Show the currently selected photo on the right side display
	 * Used for showing the image in a larger resolution,
	 * photo date, caption test, and tags.
	 */
	private void displaySelectedImage() {
		if(imageListView.getSelectionModel().isEmpty()) {
			//no photos to display
			imgbox.setImage(null);
			photo_Date.setText("Date: -/-/-");
			caption_text.setText("");
			Tags_text.setText("");
			return;
		}

		Photo selectedPhoto = imageListView.getSelectionModel().getSelectedItem();
		try {
			imgbox.setImage(selectedPhoto.getImage());
		}
		catch (IOException ioe) {
			imgbox.setImage(null);
			Alert popup = new Alert(Alert.AlertType.ERROR);
			popup.initOwner(this.mainStage);
			popup.setTitle("Image Load Error");
			popup.setHeaderText("Failed to load an image!");
			popup.setContentText("The image could not be loaded. This could be due to a change in where the image is located." +
					"\nPlease move the image back to its original directory, or delete this photo from your library.");
			popup.showAndWait();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		photo_Date.setText("Date: " + sdf.format(selectedPhoto.getDate().getTime()));

		caption_text.setText(selectedPhoto.getCap());

		String combinedTags = "";
		for(int i = 0; i < selectedPhoto.getTags().size(); i++) {
			String existingTag = selectedPhoto.getTags().get(i).getString();
			if(i == selectedPhoto.getTags().size()-1) {
				combinedTags += existingTag;
			}
			else {
				combinedTags += existingTag + ",";
			}
		}
		Tags_text.setText(combinedTags);
	}

	/**
	 * sets the list to the observable list with images thumbnails and captions
	 */
	public void loadImages() {
		photosOBSList = FXCollections.observableArrayList(this.curr_albm.getPhotos());
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
		displaySelectedImage();
	}
}
