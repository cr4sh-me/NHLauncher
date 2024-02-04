package com.cr4sh.nhlauncher.ButtonsRecycler;

// Store items data and return them
public class NHLItem {

    String category;
    String name;
    String description;
    String cmd;
    String image;
    int usage;

    public NHLItem(String category, String name, String description, String cmd, String image, int usage) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.cmd = cmd;
        this.image = image;
        this.usage = usage;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCmd() {
        return cmd;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUsage() {
        return usage;
    }


}
