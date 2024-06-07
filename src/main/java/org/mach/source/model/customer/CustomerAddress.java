package org.mach.source.model.customer;

public class CustomerAddress {
    private String floor;
    private String building;
    private String door;
    private String city;
    private String country;

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
