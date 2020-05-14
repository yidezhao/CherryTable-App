package edu.upenn.cis350.cherrytable.data.model;

import com.google.android.libraries.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Organization {
    private String name;
    private String address;
    private String contact;
    private String desc;
    private LatLng coords;
    private String[] requests;

    public Organization () {
        this.name = null;
        this.address = null;
        this.contact = null;
        this.desc = null;
        this.coords = null;
        this.requests = null;
    }

    public Organization (String name, String address, String contact, String desc, LatLng coords, String[] requests) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.desc = desc;
        this.coords = coords;
        this.requests = requests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
       this.desc = desc;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }

    public String[] getRequests() {
        return requests;
    }

    public void setRequests(String[] requests) {
        this.requests = requests;
    }
}
