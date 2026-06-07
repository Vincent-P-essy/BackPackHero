package model.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a floor of the dungeon.
 * Floor is a 5x11 grid of rooms.
 */
public class Floor {
    private static final int ROWS = 5;
    private static final int COLS = 11;

    private final Room[][] rooms;
    private final int floorNumber;
    private Room currentRoom;

    /**
     * Creates a new floor.
     * @param floorNumber the floor number
     */
    public Floor(int floorNumber) {
        if (floorNumber < 1) {
            throw new IllegalArgumentException("Floor number must be positive");
        }

        this.floorNumber = floorNumber;
        this.rooms = new Room[ROWS][COLS];
        this.currentRoom = null;
    }

    /**
     * Gets the floor number.
     * @return floor number
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Gets the number of rows.
     * @return number of rows
     */
    public int getRows() {
        return ROWS;
    }

    /**
     * Gets the number of columns.
     * @return number of columns
     */
    public int getCols() {
        return COLS;
    }

    /**
     * Sets a room at specific position.
     * @param room the room
     */
    public void setRoom(Room room) {
        Objects.requireNonNull(room);

        int row = room.getRow();
        int col = room.getCol();

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            throw new IllegalArgumentException("Invalid room position");
        }

        rooms[row][col] = room;

        // Set as current room if it's the first one
        if (currentRoom == null) {
            currentRoom = room;
            room.setVisited();
        }
    }

    /**
     * Gets a room at specific position.
     * @param row the row
     * @param col the column
     * @return the room, or null if no room
     */
    public Room getRoom(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return null;
        }
        return rooms[row][col];
    }

    /**
     * Gets the current room.
     * @return current room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Moves to a target room if possible.
     * @param targetRoom the target room
     * @return true if move successful
     */
    public boolean moveTo(Room targetRoom) {
        Objects.requireNonNull(targetRoom);

        // Check if target room exists on this floor
        if (rooms[targetRoom.getRow()][targetRoom.getCol()] != targetRoom) {
            return false;
        }

        // Check if there's a path (using simple BFS)
        if (!hasPath(currentRoom, targetRoom)) {
            return false;
        }

        currentRoom = targetRoom;
        targetRoom.setVisited();
        return true;
    }

    /**
     * Checks if there's a traversable path between two rooms.
     * Uses breadth-first search.
     * @param from starting room
     * @param to target room
     * @return true if path exists
     */
    private boolean hasPath(Room from, Room to) {
        if (from == to) {
            return true;
        }

        boolean[][] visited = new boolean[ROWS][COLS];
        List<Room> queue = new ArrayList<>();
        queue.add(from);
        visited[from.getRow()][from.getCol()] = true;

        while (!queue.isEmpty()) {
            Room current = queue.remove(0);

            if (current == to) {
                return true;
            }

            // Check adjacent rooms (up, down, left, right)
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] dir : directions) {
                int newRow = current.getRow() + dir[0];
                int newCol = current.getCol() + dir[1];

                if (newRow >= 0 && newRow < ROWS &&
                    newCol >= 0 && newCol < COLS &&
                    !visited[newRow][newCol]) {

                    Room neighbor = rooms[newRow][newCol];

                    // Allow moving to the target room directly, or through traversable rooms
                    if (neighbor != null && (neighbor == to || neighbor.canTraverse())) {
                        visited[newRow][newCol] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets all rooms on this floor.
     * @return list of rooms
     */
    public List<Room> getAllRooms() {
        List<Room> allRooms = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (rooms[i][j] != null) {
                    allRooms.add(rooms[i][j]);
                }
            }
        }
        return allRooms;
    }

    /**
     * Gets adjacent rooms to a given room.
     * @param room the room
     * @return list of adjacent rooms
     */
    public List<Room> getAdjacentRooms(Room room) {
        Objects.requireNonNull(room);

        List<Room> adjacent = new ArrayList<>();
        int row = room.getRow();
        int col = room.getCol();

        // Check all 4 directions
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < ROWS &&
                newCol >= 0 && newCol < COLS &&
                rooms[newRow][newCol] != null) {
                adjacent.add(rooms[newRow][newCol]);
            }
        }

        return adjacent;
    }
}
