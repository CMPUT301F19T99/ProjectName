package com.example.bigmood;

import android.app.Activity;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the data structure for the mood information. It implements serializable so that it can be passed
 * as an extra through intents.
 */

public class Mood implements Serializable {

    /**
     * The main type of the mood, such as Angry or Happy
     */
    private String moodTitle;
    /**
     * The description of the mood, a personal line
     */
    private String moodDescription;
    /**
     * The hex code for the color, as a string
     */
    private String moodColor;
     /**
     * The date of the mood event
     */
    private Timestamp moodDate;
    /**
     * The unique Identifier for the mood
     */
    private String moodID;
    /**
     * The link to the moodPhoto
     */
    private String moodPhoto;
    /**
     * The name of the representative emoji
     */
    private String moodEmoji;

    /**
     * This is the empty constructor, this will represent the default Mood
     * TODO:Construct default
     */
    public Mood(){}

    /**
     * This constructor is where all parameters are passed in and set in the object.
     * @param moodTitle
     * @param moodDescription
     * @param moodColor
     * @param moodDate
     */

    public Mood (String moodTitle, String moodDescription, String moodColor, Timestamp moodDate){
        this.moodTitle = moodTitle;
        this.moodDescription = moodDescription;
        this.moodColor = moodColor;
        this.moodDate = moodDate;
    }

    /**
     * Get the mood title
     * @return
     * The mood title
     */
    public String getMoodTitle() {
        return moodTitle;
    }

    /**
     * Set the mood title
     * @param moodTitle
     * The title to be set to
     */
    public void setMoodTitle(String moodTitle) {
        this.moodTitle = moodTitle;
    }
    /**
     * Get the mood photo
     * @return
     * The mood photo
     */
    public String getMoodPhoto() {
        return moodPhoto;
    }
    /**
     * Set the mood photo
     * @param moodPhoto
     * The photo to be set to
     */
    public void setMoodPhoto(String moodPhoto) {
        this.moodPhoto = moodPhoto;
    }
    /**
     * Get the mood emoji
     * @return
     * The mood emoji
     */
    public String getMoodEmoji() {
        return moodEmoji;
    }
    /**
     * Set the mood Emoji
     * @param moodEmoji
     * The emoji to be set to
     */
    public void setMoodEmoji(String moodEmoji) {
        this.moodEmoji = moodEmoji;
    }
    /**
     * Get the mood ID
     * @return
     * The mood ID
     */
    public String getMoodID() {
        return moodID;
    }
    /**
     * Set the mood ID
     * @param moodID
     * The ID to be set to
     */
    public void setMoodID(String moodID) {
        this.moodID = moodID;
    }
    /**
     * Get the mood description
     * @return
     * The mood description
     */
    public String getMoodDescription() {
        return moodDescription;
    }
    /**
     * Set the mood Description
     * @param moodDescription
     * The Description to be set to
     */
    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
    }

    /**
     * Get the mood color
     * @return
     * The mood color. Had to make this a string or else it is currently not useable
     */
    public String getMoodColor() {
        return moodColor;
    }
    /**
     * Set the mood Color
     * @param moodColor
     * The Color to be set to
     */
    public void setMoodColor(String moodColor) {
        this.moodColor = moodColor;
    }

    public Timestamp getMoodDate() {
        return moodDate;
    }
     /**
     * Set the mood Date
     * @param moodDate
     * The Date object to be set to
     */
    public void setMoodDate(Timestamp moodDate) {
        this.moodDate = moodDate;
    }
}
