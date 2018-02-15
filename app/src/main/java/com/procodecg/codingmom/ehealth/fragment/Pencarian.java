package com.procodecg.codingmom.ehealth.fragment;

/**
 * Created by macbookpro on 1/18/18.
 */

public class Pencarian {

    private int id;
    private String name;
    private String email;
    private String address;
    private String country;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getNamaDokter() {
        return name;
    }

    public void setNamaDokter(String name) {this.name = name;}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
