package com.cr4sh.nhlauncher.specialButtonsRecycler;

// Store items data and return them
public class NHLSpecialItem {
    String name;
    String description;
    String image;

    public NHLSpecialItem(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

}
