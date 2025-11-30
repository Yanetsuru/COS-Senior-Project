package com.example.demo.dungeon;

public class DungeonConfig {
    public int width = 200;
    public int height = 50;
    public int minLeafSize = 10;
    public int maxLeafSize = 20;
    public int minRoomSize = 8;
    public int maxRoomSize = 15;
    public DungeonConfig() {}
    public DungeonConfig(int width, int height,  int minLeafSize, int maxLeafSize, int minRoomSize, int maxRoomSize) {
        this.width = width;
        this.height = height;
        this.minLeafSize = minLeafSize;
        this.maxLeafSize = maxLeafSize;
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;

    }
}
