package com.cr4sh.nhlauncher;

// Store items data and return them
public class Item {

    String category;
    String name;
    String description;
    String cmd;
    String image;
    int usage;

    public Item(String category, String name, String description, String cmd, String image, int usage) {
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

    public int getUsage() {
        return usage;
    }

}
