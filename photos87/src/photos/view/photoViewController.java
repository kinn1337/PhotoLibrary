package photos.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import album.Album;
import album.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * creates a slide show page where the user can move through the image within the album
 * in a manual slideshow manner
 * @author Michael cyriac
 * @autho Russell NG
 */
public class photoViewController extends parentController implements objectTracker{
    
    @FXML
    private ImageView ImgView;
	@FXML
    private Button Next;

    @FXML
    private Button PreviousButton;

    @FXML
    private Label ViewTitleLabel;

    @FXML
    private Button returnButton;
    
    private User currUser;
    private Album curralbm;
    private int currIndx; //index of current image being shown
    
    public void initialize(){
    	   	
    	//return back to the openalbum viewing
    	 returnButton.setOnAction(e -> {
             super.mainStage = (Stage)((Node)e.getSource()).getScene().getWindow();
             List<Object> bundledObjects = new ArrayList<>();
             bundledObjects.add(this.currUser);
             bundledObjects.add(this.curralbm);
             super.switchScene(bundledObjects,"./openedAlbum.fxml", curralbm.getAlbumName().toUpperCase(Locale.ROOT));
         });
    	
    	//Next Button
    	Next.setOnAction(e->{
    		if(curralbm.numOfPhotos() > currIndx+1) {
    			currIndx++;
                try {
                    ImgView.setImage(curralbm.getPhotos().get(currIndx).getImage());
                }
                catch (IOException ioe) {
                    ImgView.setImage(null);
                    Alert popup = new Alert(Alert.AlertType.ERROR);
                    popup.initOwner(this.mainStage);
                    popup.setTitle("Image Load Error");
                    popup.setHeaderText("Failed to load an image!");
                    popup.setContentText("The image could not be loaded. This could be due to a change in where the image is located." +
                            "\nPlease move the image back to its original directory, or delete this photo from your library.");
                    popup.showAndWait();
                }
    		}
    	});
    	//looking at previous image in album
    	PreviousButton.setOnAction(e->{
    		if(currIndx-1 > -1) {
    			currIndx--;
                try {
                    ImgView.setImage(curralbm.getPhotos().get(currIndx).getImage());
                }
                catch (IOException ioe) {
                    ImgView.setImage(null);
                    Alert popup = new Alert(Alert.AlertType.ERROR);
                    popup.initOwner(this.mainStage);
                    popup.setTitle("Image Load Error");
                    popup.setHeaderText("Failed to load an image!");
                    popup.setContentText("The image could not be loaded. This could be due to a change in where the image is located." +
                            "\nPlease move the image back to its original directory, or delete this photo from your library.");
                    popup.showAndWait();
                }
    		}
    	});
    	
    	//initial image is displayed
    	//ImgView.setImage(curralbm.getPhotos().get(currIndx).getImage());
    }
    public void start(List<Object> objectList) {
    	currUser = (User)objectList.get(0);
        curralbm = (Album)objectList.get(1);
        currIndx = (int)objectList.get(2);
        //initial image display
        try {
            ImgView.setImage(curralbm.getPhotos().get(currIndx).getImage());
        }
        catch (IOException ioe) {
            ImgView.setImage(null);
            Alert popup = new Alert(Alert.AlertType.ERROR);
            popup.initOwner(this.mainStage);
            popup.setTitle("Image Load Error");
            popup.setHeaderText("Failed to load an image!");
            popup.setContentText("The image could not be loaded. This could be due to a change in where the image is located." +
                    "\nPlease move the image back to its original directory, or delete this photo from your library.");
            popup.showAndWait();
        }
    	
    }

}
