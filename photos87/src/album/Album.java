package album;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.*;

import javafx.scene.image.Image;

/**
 * creates a album object where we can store
 * photo objects along with the albums name 
 * @author Michael cyriac
 * @autho Russell NG
 */
public class Album implements Serializable{

	private String albumName;
	private List<Photo> photos;
	
	/**
	 * a album is initialied with a its name
	 * @param aName name of the album
	 */
	public Album(String aName) {
		this.albumName = aName;
		photos = new ArrayList<Photo>();
	}
	
	/**
	 * adds a photo to the album's list of photos
	 * @param imgLoc image of photo object
	 * @param caption caption of the photo being added
	 * @param tags tags of the photo being added
	 */
	public void addphoto(String imgLoc, String caption, String tags) throws IOException {
		this.photos.add(new Photo(imgLoc, caption, tags));
	}
	/**
	 * addes a photo object
	 * @param img photo object that was already initialized
	 */
	public void addObjPhoto(Photo img) {
		this.photos.add(img);
	}

	/**
	 * delets a photo from an album
	 * @param img the photo to remove
	 */
	public void removePhoto(Photo img) {
		this.photos.remove(img);
	}
	
	/**
	 * sets the oldest photo in the album
	 */
	public void setOld() {
		//go through full Phots array and find the one with the onldes date
		//oldestImg = ;
	}
	
	/**
	 * sets the newest photo within the album
	 */
	public void setnew() {
		// go throught photos and set the  newest to this
		//newestImg = ;
	}

	/**
	 * gives this albums name
	 * @return this albums name is returned
	 */
	public String getAlbumName() {
		return this.albumName;
	}

	/**
	 * check if a specified photo using its path exists in the album
	 * @param photoPath the path of the image in the photo object
	 * @return the photo that has the specified path or null if one was not found
	 */
	public Photo photoExists(String photoPath) {
		//if a matching photo is found, returns the photo, according to photo path matching
		for(Photo existingPhoto : this.photos) {
			if(existingPhoto.getImageLocation().equals(photoPath)) {
				return existingPhoto;
			}
		}
		return null;
	}

	/**
	 * checks if the specified photo object is in this album
	 * @param img the photo object to be checked if its in the albu
	 * @return a boolean indicating true if the photo object is also in the album
	 */
	public Boolean photoExists(Photo img) {
		for(Photo existingPhoto : this.photos) {
			if (existingPhoto.equals(img)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * the daterange of the images of this album
	 * @return the date in the range of the photos
	 */
	public String dateRange() {
		Calendar earliestDate = null;
		Calendar latestDate = null;

		for(Photo existingPhoto : this.photos) {
			Calendar existingPhotoDate = existingPhoto.getDate();

			if(earliestDate == null && latestDate == null) {
				earliestDate = existingPhotoDate;
				latestDate = existingPhotoDate;
			}

			if(existingPhotoDate.compareTo(earliestDate) < 0) {
				earliestDate = existingPhotoDate;
			}

			if(existingPhotoDate.compareTo(latestDate) > 0) {
				latestDate = existingPhotoDate;
			}
		}

		if(earliestDate == null && latestDate == null) {
			return "No dates found";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		return sdf.format(earliestDate.getTime()) + " - " + sdf.format(latestDate.getTime());
	}

	/**
	 * the number of photos within this album
	 * @return the int number of photos within this album
	 */
	public int numOfPhotos() {
		return this.photos.size();
	}
	/**
	 * gives the list of all the photos within this album
	 * @return the list<> of all the photos in this album
	 */
	public List<Photo> getPhotos(){
		return this.photos;
	}

	/**
	 * the new name is given to this album
	 * @param newname thew sting name of this album
	 */
	public void editAlbumName(String newname) {
		this.albumName = newname;
	}
	

}
