package com.Karan.contact.Model;

public class ContactModel {
    private String id;
    private String firstName;
    private String lastName;
    private String Email;
    private String PhoneNumber;
    private String image;
    private boolean isFavourite;

    public ContactModel(String id, String firstName, String lastName, String email, String phoneNumber, String image, boolean isFavourite) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        Email = email;
        PhoneNumber = phoneNumber;
        this.image = image;
        this.isFavourite = isFavourite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
