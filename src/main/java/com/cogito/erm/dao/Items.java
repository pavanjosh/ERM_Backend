package com.cogito.erm.dao;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document(collection = "items")
public class Items {

    @Id
    private String id;

    private String itemName;
    private ItemCategories category;
    private Instant time;
    private String description;
    private String action;
    private String notes;
    private List<String> infoSection;
    private boolean status;


    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getInfoSection() {
        return infoSection;
    }

    public void setInfoSection(List<String> infoSection) {
        this.infoSection = infoSection;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ItemCategories getCategory() {
        return category;
    }

    public void setCategory(ItemCategories category) {
        this.category = category;
    }
}
