package com.example.demo.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Leaf {
    public int x;
    public int y;
    public int width;
    public int height;
    public Leaf leftChild;
    public Leaf rightChild;
    public Room room;
    public List<Room> halls = new ArrayList<>();
    private Random random = new Random();
    private DungeonConfig dungeonConfig;
    public Leaf(int x, int y, int width, int height, DungeonConfig dungeonConfig) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dungeonConfig = dungeonConfig;
    }
    public boolean split() {
        if (leftChild != null || rightChild != null)
            return false; // already split

        boolean splitH = random.nextBoolean();

        if (width > height && width / (double)height >= 1.25)
            splitH = false;
        else if (height > width && height / (double)width >= 1.25)
            splitH = true;

        int max = (splitH ? height : width);

        // must be large enough to split
        if (max < dungeonConfig.minLeafSize * 2)
            return false;

        int splitPoint = dungeonConfig.minLeafSize +
                random.nextInt(max - dungeonConfig.minLeafSize * 2);

        if (splitH) {
            leftChild = new Leaf(x, y, width, splitPoint, dungeonConfig);
            rightChild = new Leaf(x, y + splitPoint, width, height - splitPoint, dungeonConfig);
        } else {
            leftChild = new Leaf(x, y, splitPoint, height, dungeonConfig);
            rightChild = new Leaf(x + splitPoint, y, width - splitPoint, height, dungeonConfig);
        }

        return true;
    }


    public void createRooms() {
        if(leftChild != null || rightChild != null){
            if (leftChild != null) {
                leftChild.createRooms();
            }
            if (rightChild != null) {
                rightChild.createRooms();
            }

            if (leftChild != null && rightChild != null) {
                Room roomA = leftChild.room;
                Room roomB = rightChild.room;
                if (roomA != null && roomB != null) {
                    createHall(roomA, roomB);
                }
            }
        }
        else {
            int roomWidth = random.nextInt(dungeonConfig.maxRoomSize -  dungeonConfig.minRoomSize) + dungeonConfig.minRoomSize;
            int roomHeight = random.nextInt(dungeonConfig.maxRoomSize - dungeonConfig.minRoomSize) + dungeonConfig.minRoomSize;

            int padding = 1;
            int roomX = x + padding + random.nextInt(Math.max(1, width - roomWidth - 2*padding));
            int roomY = y + padding + random.nextInt(Math.max(1, height - roomHeight - 2*padding));

            room = new Room(roomX, roomY, roomWidth, roomHeight);
        }
    }

    private void createHall(Room a, Room b) {
        int x1 = a.centerX();
        int x2 = b.centerX();
        int y1 = a.centerY();
        int y2 = b.centerY();

        if(Math.random() < 0.5){
            halls.add(new Room(Math.min(x1, x2), y1, Math.abs(x1 - x2) + 1, 1));
            halls.add(new Room(x2, Math.min(y1, y2), 1, Math.abs(y1 - y2) + 1));
        } else{
            halls.add(new Room(x1, Math.min(y1, y2), 1, Math.abs(y1 - y2) + 1));
            halls.add(new Room(Math.min(x2, x1), y2, Math.abs(x1 - x2) + 1, 1));
        }

    }

    public Room getRoom() {
        if (room != null)
            return room;
        Room left = leftChild != null ? leftChild.getRoom() : null;
        Room right = rightChild != null ? rightChild.getRoom() : null;

        if (left == null && right == null)
            return null;
        else if (right == null)
            return left;
        else if (left == null)
            return right;
        else
            return Math.random() < 0.5 ? left : right;
    }


}
