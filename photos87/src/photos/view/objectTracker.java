package photos.view;

import java.util.List;

/**
 * meant to have a stat function that allows to set
 * any information that is meant to be passes from one scene to the next
 * @author Michael cyriac
 * @autho Russell NG
 */
public interface objectTracker {
    /**
     * this is called everytime a scene is changed
     * so that we can transfer information from one scene to the next
     * @param objectList the objects to be transferend to the next scene
     */
    public abstract void start(List<Object> objectList); //list of any object references that will be necessary
}