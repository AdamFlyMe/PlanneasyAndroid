package me.adamfly.planneasy;

/**
 * Created by adamf on 5/18/2017.
 */

public class Entry {
    private String entryId, title, category, description, color;
    public Entry(String entryId, String title, String category, String description, String color){
        this.entryId = entryId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.color = color;
    }

    public String getEntryId(){
        return this.entryId;
    }

    public String getTitle(){
        return this.title;
    }

    public String getCategory(){
        return this.category;
    }

    public String getDescription(){
        return this.description;
    }

    public String getColor(){
        return this.color;
    }

    public void setEntryId(String entryId){
        this.entryId = entryId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setColor(String color){
        this.color = color;
    }

}
