package com.example.filescanner.BEE;

import java.util.ArrayList;
import java.util.List;

public class Box {

    private int id;
    private int userId;
    private List<Document> documents = new ArrayList<>();

    public Box(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public void clearDocuments() {
        documents.clear();
    }
}
