package com.cr4sh.nhlanucher;

public class Item {

    String category;
    String name;
    String description;
    String cmd;
    int image;

    public Item(String category, String name, String description, String cmd,  int image) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.cmd = cmd;
        this.image = image;
    }

    public String getCategory() { return category;}

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getImage() {
        return image;
    }

    public String getCmd(){ return cmd; }

}
