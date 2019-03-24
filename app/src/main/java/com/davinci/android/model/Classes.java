package com.davinci.android.model;

import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class Classes {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "drawable")
    private String drawable;

    @ColumnInfo(name = "unity")
    private int unity;

    @ColumnInfo(name = "rounds")
    private int rounds;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "exam")
    private boolean exam;

    @Ignore
    private List<Links> links;

    public Classes(int id, String title, String description, String drawable, int unity, int rounds, long date, boolean exam) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.drawable = drawable;
        this.unity = unity;
        this.rounds = rounds;
        this.date = date;
        this.exam = exam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnity() {
        return unity;
    }

    public void setUnity(int unity) {
        this.unity = unity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
        this.drawable = drawable;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isExam() {
        return exam;
    }

    public void setExam(boolean exam) {
        this.exam = exam;
    }

    public List<Links> getLinks() {
        return links;
    }

    public void setLinks(List<Links> links) {
        this.links = links;
    }
}