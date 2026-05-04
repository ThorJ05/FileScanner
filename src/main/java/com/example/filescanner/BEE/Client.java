package com.example.filescanner.BEE;

import java.util.List;

public class Client {
    private int id;
    private String companyName;

    public Client(int id, String companyName) {
        this.id = id;
        this.companyName = companyName;
    }

    public int getId() { return id; }
    public String getCompanyName() { return companyName; }
}
