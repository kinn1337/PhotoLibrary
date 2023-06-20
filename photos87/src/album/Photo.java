package album;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.io.*;
import javafx.scene.image.Image;
/**
 * creates a photo object that has a field for list of tags, caption, date and image
 * @author Michael cyriac
 * @autho Russell NG
 */
public class Photo implements Serializable{

	private String imageLocation;
	private Calendar date;
	private String caption;
	private List<Tag> tags; // might need to change
	
	/**
	 * initializes the photo that gives it its caption tag and image to hold
	 * @param imgLoc the absolute path to the image file
	 * @param caption the caption of that photo
	 * @param tagInput the tags that that photo has
	 */
	public Photo(String imgLoc, String caption, String tagInput) throws IOException { //recieves string that it splits
													// creates new instances of tags
		this.imageLocation = imgLoc;
		this.caption = caption;
		this.setDate();

		this.tags = new ArrayList<>();
		if(!this.validTagInput(tagInput) || tagInput.isEmpty()) {
			return;
		}

		String[] tgs = tagInput.split(",");
		for(String temp : tgs) {
			this.tags.add(new Tag(temp));
		}
	}

	/**
	 * Retrieves the last modified time of an image file,
	 * and then sets the Photo object's date to that time.
	 */
	public void setDate() throws IOException {
		BasicFileAttributes photoAttributes = Files.readAttributes(Paths.get(this.imageLocation), BasicFileAttributes.class);
		this.date = Calendar.getInstance();
		this.date.setTimeInMillis(photoAttributes.lastModifiedTime().toMillis());
		this.date.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * gives the date
	 * @return this photos last edited date is given
	 */
	public Calendar getDate() {
		return this.date;
	}
	
	/**
	 * the caption of this photo is returned
	 * @return this photos caption is given
	 */
	public String getCap() {
		return this.caption;
	}
	/**
	 * the image of this photo is given
	 * @return the photos image is given
	 */
	public String getImageLocation() {
		return this.imageLocation;
	}

	/**
	 * convers the path of the image in this photo to a Image object that was be used for
	 * a Image View
	 * @return a Image Obejct using the file path of the image belonging to this photo
	 * @throws IOException
	 */
	public Image getImage() throws IOException {
		FileInputStream imageByteReader = new FileInputStream(this.imageLocation);
		return new Image(imageByteReader);
	}
	/**
	 * changes the current caption to what the user wantes to change the caption to
	 * @param caption the new caption to change to
	 */
	public void editCaption(String caption) {
		this.caption = caption;
	}
	/**
	 * a way to edit the tags of the photo
	 * @param StrTags the new tags of this photo
	 */
	public void newtags(String StrTags) {
		//removes all tags
		if(!this.validTagInput(StrTags)) {
			return;
		}
		this.tags.clear();
		if(StrTags.isEmpty()) {
			return;
		}
		//lable eveything in the tags box as the new tags
		String[] tgs;
		tgs = StrTags.split(",");
		for(String temp : tgs) {
			this.tags.add(new Tag(temp));
		}
	}
	/**
	 * 
	 * @return a list of this photos tags are given
	 */
	public List<Tag> getTags(){
		return this.tags;
	}
	/**
	 * the tags are returned in a string format
	 * @return a string of this photos tags are given
	 */
	public String getStringTags() {
		String StrTags = "";
		for(Tag temp : this.tags) {
			StrTags = temp.getString() +", ";
		}
		return StrTags;
	}
	/**
	 * checks if this photo has the tag indicated
	 * @param check the tag to check if this photo has
	 * @return a boolean true if this given tag belongs to this photo
	 */
	public boolean hasTage(Tag check) {
		
		for(Tag temp : this.tags) {
			if(temp.equalsTag(check)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * chekcs if the tag entered is in the correct format a tag is alloed to be in
	 * such as t1=v1 , t2= v2
	 * @param tagInput the sting of all the tags the user wants to give an image
	 * @return a boolean true if the string is in a valid tag format
	 */
	private Boolean validTagInput(String tagInput) {
		int equalCount = (int)tagInput.chars().filter(ch -> ch == '=').count();
		int commaCount = (int)tagInput.chars().filter(ch -> ch == ',').count();
		if(tagInput.isEmpty()) {
			return true;
		}

		if(equalCount != commaCount + 1) {
			return false;
		}

		String[] tgs = tagInput.split(",");
		for(String temp : tgs) {
			String[] formatCheck = temp.split("=");
			if(formatCheck.length != 2) {
				return false;
			}
		}

		return true;
	}
}
