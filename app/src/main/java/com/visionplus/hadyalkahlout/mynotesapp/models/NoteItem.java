package com.visionplus.hadyalkahlout.mynotesapp.models;

public class NoteItem {

    private String id, categoryId, title, description;
    private int done;

    public NoteItem(String id, String categoryId, String title, String description, int done) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
