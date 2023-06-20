package album;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * creates a User who is serializable
 * who has albums withc photos
 * @author Michael cyriac
 * @autho Russell NG
 */
public class User implements Serializable{
	private String usrID;
	private String usrPassword;
	private List<Album> albums;
	
	/**
	 * initializes a user with a name and password
	 * @param usrID the users username
	 * @param usrPassword the users password
	 */
	public User(String usrID, String usrPassword){
		this.usrID = usrID;
		this.usrPassword = usrPassword;
		this.albums = new ArrayList<>();
	}
	/**
	 * @return gives the users username
	 */
	public String getID() {
		return this.usrID;
	}
	/**
	 * @return gives the users password
	 */
	public String getPassword() {
		return this.usrPassword;
	}
	/**
	 * @return gives all the albums that are owned by this user
	 */
	public List<Album> getAlbums() {
		return this.albums;
	}

	public Album getAlbum(String aName) {
		for(Album existingAlbum : this.albums) {
			if(existingAlbum.getAlbumName().equalsIgnoreCase(aName)) {
				return existingAlbum;
			}
		}

		return null;
	}
	/**
	 * checks all the albums of this user to see if the paramater album is owned by this user
	 * @param aName the album name to check if its matches with this user
	 * @return a booleans true if the user has the given album
	 */
	public Boolean albumExists(String aName) {
		if(this.albums.isEmpty()) {
			return false;
		}
		for(Album existingAlbum : this.albums) {
			if(existingAlbum.getAlbumName().equalsIgnoreCase(aName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * checks if the user owns a perticular photo
	 * @param photoPath the path of the photo to see if the user own it
	 * @return a valid photo object if the user owns a image with the specified path
	 */
	public Photo photoExists(String photoPath) {
		//to see if a photo already exists within one of a user's albums
		if(this.albums.isEmpty()) {
			return null;
		}
		for(Album existingAlbum : this.albums) {
			Photo potentialDuplicate = existingAlbum.photoExists(photoPath);
			if(potentialDuplicate != null) {
				return potentialDuplicate;
			}
		}

		return null;
	}
	/**
	 * add a new album to the users list of albums
	 * @param album the albums to be added
	 */
	public void addAlbum(Album album) {
		this.albums.add(album);
	}
	/**
	 * removes the given album
	 * @param ind the int  index to be removed from albums
	 */
	public void removeAlbum(int ind) {
		this.albums.remove(ind);
	}
}
