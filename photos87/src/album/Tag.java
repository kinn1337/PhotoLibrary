package album;

import java.io.*;

/**
 * the tags that hold the type and value of a tag in the format: t1 = v1
 * @author Michael cyriac
 * @autho Russell NG
 *
 */
public class Tag implements Serializable{
	private String tag;
	private String val;
	
	/**
	 * a string of the tags the user givens for this photo
	 * @param UsrInput the string of tags the user gives
	 */
	public Tag(String UsrInput) {
		
		String[] newTag = UsrInput.split("=");
		this.tag = newTag[0].strip(); //spliting each field where we find "="
		this.val = newTag[1].strip();

	}
	
	/**
	 * 
	 * @return the type of the tag is given
	 */
	public String getTag() {
		return this.tag;
	}
	
	/**
	 * @return the value of the tag is given
	 */
	public String getVal() {
		return this.val;
	}
	
	/**
	 * @return the type and value of the tag are given in a in the format t1 = v1
	 */
	public String getString() {
		return this.tag+"="+ this.val;
	}
	
	/**
	 * check if the given tag and this tag are the same
	 * @param tagcheck the tag to check if we have
	 * @return a boolean where true mean this and the paramater tag are the same
	 */
	public boolean equalsTag(Tag tagcheck) {

		if(this.tag.equals(tagcheck.getTag()) && this.val.equals(tagcheck.getVal())) {
			return true;
		}
		else {
			return false;
		}
	}

}
