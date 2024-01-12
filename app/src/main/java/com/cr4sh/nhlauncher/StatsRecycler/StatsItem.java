package com.cr4sh.nhlauncher.StatsRecycler;

// Store items data and return them
public class StatsItem {

    String name;
    String image;
    String usage;

    public StatsItem(String name, String image, String usage) {
        this.name = name;
        this.image = image;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUsage() {
        return usage;
    }

}
