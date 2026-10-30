package com.example.nextnest;

import javafx.scene.image.Image;

import java.io.Serializable;

public class Post implements Serializable {

    String title;
    String discription;
    String price;
    String house_no;
    String PhnNo;
    String address;
    String mail;
    Image image;
    Image temp;

    public Post(String title, String discription, String price, String house_no, String phnNo, String address, String mail, Image image, Image temp) {
        this.title = title;
        this.discription = discription;
        this.price = price;
        this.house_no = house_no;
        PhnNo = phnNo;
        this.address = address;
        this.mail = mail;
        this.image = image;
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "Post{" + "title='" + title + '\'' + ", discription='" + discription + '\'' + ", price='" + price + '\'' + ", house_no='" + house_no + '\'' + ", PhnNo='" + PhnNo + '\'' + ", address='" + address + '\'' + ", mail='" + mail + '\'' + ", image=" + image + '\'' + ", temp=" + temp + '}';
    }
}
