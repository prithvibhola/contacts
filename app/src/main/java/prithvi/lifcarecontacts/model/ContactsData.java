package prithvi.lifcarecontacts.model;

import java.io.Serializable;

/**
 * Created by Prithvi on 3/25/2017.
 */

public class ContactsData implements Serializable {

    private String name;
    private String mobileNumber;
    private String imageUrl;
    private String mimeType;

    private String rawContactID;

    public ContactsData(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getRawContactID() {
        return rawContactID;
    }

    public void setRawContactID(String rawContactID) {
        this.rawContactID = rawContactID;
    }
}
