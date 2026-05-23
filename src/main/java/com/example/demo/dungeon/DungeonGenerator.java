package com.example.demo.dungeon;

import java.util.ArrayList;
import java.util.List;

public class DungeonGenerator {

    private DungeonConfig config;
    private char[][] map;
    private List<Leaf> leaves = new ArrayList<>();

    public DungeonGenerator(DungeonConfig config) {
        this.config = config;
        map = new char[config.height][config.width];
    }

    public char[][] generate() {
        // Initialize map with walls
        for (int y = 0; y < config.height; y++)
            for (int x = 0; x < config.width; x++)
                map[y][x] = '#';

        // Build initial leaf
        Leaf root = new Leaf(0, 0, config.width, config.height, config);
        leaves.add(root);

        boolean splitPossible = true;
        while (splitPossible) {
            splitPossible = false;
            for (Leaf leaf : new ArrayList<>(leaves)) {
                if (leaf.leftChild == null && leaf.rightChild == null) {
                    if (leaf.width > config.maxLeafSize || leaf.height > config.maxLeafSize) {
                        if (leaf.split()) {
                            leaves.add(leaf.leftChild);
                            leaves.add(leaf.rightChild);
                            splitPossible = true;
                        }
                    }
                }
            }
        }

        // Create rooms
        root.createRooms();

        // Draw rooms and halls
        for (Leaf leaf : leaves) {
            if (leaf.room != null)
                drawRoom(leaf.room);
            for (Room hall : leaf.halls)
                drawRoom(hall);
        }

        return map;
    }

    private void drawRoom(Room room) {
        for (int y = room.y; y < room.y + room.height; y++)
            for (int x = room.x; x < room.x + room.width; x++)
                if (y >= 0 && y < config.height && x >= 0 && x < config.width)
                    map[y][x] = '.';
    }
}
