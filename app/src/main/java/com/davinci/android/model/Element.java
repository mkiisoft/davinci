package com.davinci.android.model;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class Element {

    @Embedded
    private Classes item;
    @Relation(parentColumn = "id", entityColumn = "fkid")
    private List<Links> links;

    public Element(Classes item, List<Links> links) {
        this.item = item;
        this.links = links;
    }

    public Classes getItem() {
        return item;
    }

    public void setItem(Classes items) {
        this.item = items;
    }

    public List<Links> getLinks() {
        return links;
    }

    public void setLinks(List<Links> links) {
        this.links = links;
    }
}
