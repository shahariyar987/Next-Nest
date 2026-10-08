package com.example.nextnest;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private int id; // To align with a database primary key
    private String name;
    private String number;
    private String gender;
    private String email;
    private String address;
    private String nid;
    private String password;
    private String uuid; // Optional if the database will handle unique IDs

    // No-args constructor (required for database frameworks)
    public User() {
    }

    public User(String number,
                String password){
        this.number = number;
        this.password = password;
    }

    // All-args constructor
    public User(String name, String number, String gender, String email, String address, String nid, String password) {
        this.name = name;
        this.number = number;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.nid = nid;
        this.password = password;
        this.uuid = UUID.randomUUID().toString();
    }

    // Getter and Setter Methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", number='" + number + '\'' + ", gender='" + gender + '\'' + ", email='" + email + '\'' + ", address='" + address + '\'' + ", nid='" + nid + '\'' + ", password='" + password + '\'' + ", uuid='" + uuid + '\'' + '}';
    }
}
