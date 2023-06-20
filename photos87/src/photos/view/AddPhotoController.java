package photos.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Database.Database;
import album.Album;
import album.Photo;
import album.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert; 
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * this is the controler for the AddPhoto.fxml
 * that presents the user with a screen 
 * with a upload button to upload an image
 * and to given the image a compation and tags
 * @author Michael cyriac
 * @autho Russell NG
 */
public class AddPhotoController extends parentController implements objectTracker{

    @FXML private ImageView imagePreview;
    @FXML private TextArea tagField;
	@FXML private TextArea captionField;
    @FXML private Button cancelButton;
    @FXML private Button uploadButton;
	@FXML private Button confirmButton;

    private User current_user;
	private Album curr_albm;
	private File selectedImage; //holds path from uploadButton
    
    /**
     * sets up the button controls to cancel the add photo
     * to upload a photo from a perticular location
     * and confirm the add photo
     * 
     */
    public void initialize() {
    	cancelButton.setOnAction(e -> {
    		super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
    		List<Object> bundledObjects = new ArrayList<>();
    		bundledObjects.add(this.current_user);
			bundledObjects.add(this.curr_albm);
            super.switchScene(bundledObjects,"./openedAlbum.fxml", this.curr_albm.getAlbumName().toUpperCase(Locale.ROOT));
		});
    	
    	uploadButton.setOnAction(e->{
    		FileChooser uploadPath = new FileChooser();
    		selectedImage = uploadPath.showOpenDialog(mainStage);
    		if(selectedImage != null) {
    			//if its a valid path, check if it is a valid image first, then we display the image they have
				try {
					FileInputStream imageLoader = new FileInputStream(selectedImage.getAbsolutePath());
					Image img = new Image(imageLoader);
					if(img.isError()) {
						Alert popup = new Alert(Alert.AlertType.ERROR);
						popup.initOwner(super.mainStage);
						popup.setTitle("Error");
						popup.setHeaderText("Invalid image file");
						popup.setContentText("Please make sure a valid image file was chosen");
						popup.showAndWait();
						selectedImage = null;
						imagePreview.setImage(null);
						return;
					}
					imagePreview.setImage(img);
				}
				catch (IOException ioe) {
					Alert popup = new Alert(Alert.AlertType.ERROR);
					popup.initOwner(this.mainStage);
					popup.setTitle("File Input Stream Error");
					popup.setHeaderText("File Input Stream could not read selected file!");
					popup.setContentText("The file you selected is unreadable, please choose a new one.");
					popup.showAndWait();
					return;
				}
    		}
    	});

		//adding a photo to the album
		confirmButton.setOnAction(e -> {
			if(selectedImage == null) { //invalid meaning we have no image, so it's an error
				Alert popup = new Alert(Alert.AlertType.ERROR);
				popup.initOwner(super.mainStage);
				popup.setTitle("Error");
				popup.setHeaderText("Invalid file path");
				popup.setContentText("Please make sure a valid image file was chosen");
				popup.showAndWait();
				return;
			}

			Photo newPhoto = this.current_user.photoExists(selectedImage.getAbsolutePath());
			if(newPhoto != null) {
				newPhoto.editCaption(captionField.getText());
				newPhoto.newtags(tagField.getText());
				if(!this.curr_albm.photoExists(newPhoto)) {
					this.curr_albm.addObjPhoto(newPhoto);
				}
			}
			else {
				//photo has not been imported
				try {
					newPhoto = new Photo(selectedImage.getAbsolutePath(), captionField.getText(), tagField.getText());
					this.curr_albm.addObjPhoto(newPhoto);
				}
				catch (IOException ioe) {
					Alert popup = new Alert(Alert.AlertType.ERROR);
					popup.initOwner(this.mainStage);
					popup.setTitle("Image Read Error");
					popup.setHeaderText("Failed to read image file or image file metadata (Date, etc.)!");
					popup.setContentText("The selected photo could not be read due to either a bad path or bad file type.");
					popup.showAndWait();
					return;
				}
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
			super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
			List<Object> bundledObjects = new ArrayList<>();
			bundledObjects.add(this.current_user);
			bundledObjects.add(this.curr_albm);
			super.switchScene(bundledObjects,"./openedAlbum.fxml", this.curr_albm.getAlbumName().toUpperCase(Locale.ROOT));
		});
    }

	public void start(List<Object> objectList) {
		this.current_user = (User)objectList.get(0);
		this.curr_albm = (Album)objectList.get(1);
	}
}
