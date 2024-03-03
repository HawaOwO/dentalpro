package com.example.dentalpro;

public class Record {
    private String username;
    private String name;
    private String date;
    private String quantityR;

    public Record(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String Username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantityR() {
        return quantityR;
    }

    public void setQuantityR(String quantityR) {
        this.quantityR = quantityR;
    }
}
