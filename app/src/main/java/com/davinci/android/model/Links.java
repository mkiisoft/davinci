package com.davinci.android.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "links",
        foreignKeys = @ForeignKey(
                entity = Classes.class,
                parentColumns = "id",
                childColumns = "fkid",
                onDelete = CASCADE
        ), indices = @Index(value = "fkid"))
public class Links {

    @PrimaryKey
    @ColumnInfo(name = "linkId")
    private int linkId;

    @ColumnInfo(name = "fkid")
    private int fkid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "link")
    private String link;

    public Links(int linkId, int fkid, String name, String link) {
        this.link = link;
        this.fkid = fkid;
        this.name = name;
        this.link = link;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public int getFkid() {
        return fkid;
    }

    public void setFkid(int fkid) {
        this.fkid = fkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
