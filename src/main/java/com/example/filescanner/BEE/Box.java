package com.example.filescanner.BEE;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private final List<Document> documents = new ArrayList<>();

    public void addDocument(Document doc) {
        documents.add(doc);
    }

    public List<Document> getDocuments() {
        return documents;
    }
}
