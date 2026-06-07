package model.dungeon;

/**
 * Represents a room in the dungeon.
 */
public class Room {
    private final RoomType type;
    private final int row;
    private final int col;
    private boolean visited;
    private RoomContent content;

    /**
     * Room types in the dungeon.
     */
    public enum RoomType {
        CORRIDOR,      // Empty corridor
        ENEMY,         // Contains enemies
        TREASURE,      // Contains treasure
        MERCHANT,      // Contains a merchant
        HEALER,        // Contains a healer
        EXIT,          // Exit to next floor
        LOCKED_GATE,   // Locked gate (requires key)
        SURPRISE       // Surprise encounter
    }

    /**
     * Creates a new room.
     * @param type the room type
     * @param row the row position
     * @param col the column position
     */
    public Room(RoomType type, int row, int col) {
        if (type == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (row < 0 || row >= 5 || col < 0 || col >= 11) {
            throw new IllegalArgumentException("Invalid room position");
        }

        this.type = type;
        this.row = row;
        this.col = col;
        this.visited = false;
        this.content = null;
    }

    /**
     * Gets the room type.
     * @return the type
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Gets the row position.
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column position.
     * @return the column
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks if this room has been visited.
     * @return true if visited
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Marks this room as visited.
     */
    public void setVisited() {
        this.visited = true;
    }

    /**
     * Gets the room content.
     * @return the content
     */
    public RoomContent getContent() {
        return content;
    }

    /**
     * Sets the room content.
     * @param content the content
     */
    public void setContent(RoomContent content) {
        this.content = content;
    }

    /**
     * Checks if this room can be traversed without interaction.
     * @return true if can traverse
     */
    public boolean canTraverse() {
        return type == RoomType.CORRIDOR ||
               type == RoomType.TREASURE ||
               type == RoomType.MERCHANT ||
               type == RoomType.HEALER ||
               type == RoomType.EXIT ||
               (type == RoomType.ENEMY && visited) ||
               (type == RoomType.LOCKED_GATE && visited);
    }
}
