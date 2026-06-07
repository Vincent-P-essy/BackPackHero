package model;

import model.Cell.PlacedEquipment;
import model.equipment.Equipment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the hero's backpack using a Cell[][] grid.
 * Refactored according to professor's feedback to use Cell interface.
 */
public class Backpack {
    private static final int MAX_ROWS = 5;
    private static final int MAX_COLS = 7;

    private final Cell[][] grid;
    private int rows;
    private int cols;

    /**
     * Creates a new backpack with specified dimensions.
     * @param rows number of rows (max 5)
     * @param cols number of columns (max 7)
     */
    public Backpack(int rows, int cols) {
        if (rows < 1 || rows > MAX_ROWS) {
            throw new IllegalArgumentException("Rows must be between 1 and " + MAX_ROWS);
        }
        if (cols < 1 || cols > MAX_COLS) {
            throw new IllegalArgumentException("Columns must be between 1 and " + MAX_COLS);
        }

        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[MAX_ROWS][MAX_COLS];

        // Initialize grid: available cells as Free, rest as Blocked
        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (i < rows && j < cols) {
                    grid[i][j] = new Cell.Free();
                } else {
                    grid[i][j] = new Cell.Blocked();
                }
            }
        }
    }

    /**
     * Gets the number of rows.
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns.
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gets the cell at the specified position.
     * @param row the row
     * @param col the column
     * @return the cell
     */
    public Cell getCell(int row, int col) {
        if (row < 0 || row >= MAX_ROWS || col < 0 || col >= MAX_COLS) {
            return new Cell.Blocked();
        }
        return grid[row][col];
    }

    /**
     * Checks if a cell is available.
     * @param row the row
     * @param col the column
     * @return true if available, false otherwise
     */
    public boolean isCellAvailable(int row, int col) {
        return getCell(row, col).isAvailable();
    }

    /**
     * Gets all equipment in the backpack.
     * @return list of placed equipment
     */
    public List<PlacedEquipment> getEquipment() {
        List<PlacedEquipment> equipment = new ArrayList<>();
        List<PlacedEquipment> seen = new ArrayList<>();

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (grid[i][j] instanceof Cell.Occupied occupied) {
                    PlacedEquipment pe = occupied.placedEquipment();
                    // Only add each equipment once (not for each cell it occupies)
                    if (!seen.contains(pe)) {
                        equipment.add(pe);
                        seen.add(pe);
                    }
                }
            }
        }

        return equipment;
    }

    /**
     * Tries to place an equipment in the backpack.
     * @param eq the equipment to place
     * @param row the starting row
     * @param col the starting column
     * @param rotation the rotation (0, 90, 180, 270 degrees)
     * @return true if placement successful, false otherwise
     */
    public boolean placeEquipment(Equipment eq, int row, int col, int rotation) {
        Objects.requireNonNull(eq);

        if (rotation < 0 || rotation % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90");
        }

        // Check if equipment can be placed
        if (!canPlaceEquipment(eq, row, col, rotation)) {
            return false;
        }

        // Place equipment by updating cells
        PlacedEquipment placed = new PlacedEquipment(eq, row, col, rotation);
        boolean[][] shape = eq.getRotatedShape(rotation);

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    grid[row + i][col + j] = new Cell.Occupied(placed);
                }
            }
        }

        return true;
    }

    /**
     * Checks if an equipment can be placed.
     * @param eq the equipment
     * @param row the starting row
     * @param col the starting column
     * @param rotation the rotation
     * @return true if can be placed, false otherwise
     */
    public boolean canPlaceEquipment(Equipment eq, int row, int col, int rotation) {
        Objects.requireNonNull(eq);

        boolean[][] shape = eq.getRotatedShape(rotation);

        // Check if equipment fits in available space
        if (row < 0 || col < 0 ||
            row + shape.length > rows ||
            col + shape[0].length > cols) {
            return false;
        }

        // Check if all required cells are available
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] && !grid[row + i][col + j].isAvailable()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Removes an equipment from the backpack.
     * @param placedEq the placed equipment to remove
     * @return true if removal successful, false otherwise
     */
    public boolean removeEquipment(PlacedEquipment placedEq) {
        Objects.requireNonNull(placedEq);

        // Check if equipment exists in backpack
        boolean found = false;
        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (grid[i][j] instanceof Cell.Occupied occupied) {
                    if (occupied.placedEquipment().equals(placedEq)) {
                        found = true;
                        break;
                    }
                }
            }
            if (found) break;
        }

        if (!found) {
            return false;
        }

        // Free all cells occupied by this equipment
        boolean[][] shape = placedEq.equipment().getRotatedShape(placedEq.rotation());
        int row = placedEq.row();
        int col = placedEq.col();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j]) {
                    grid[row + i][col + j] = new Cell.Free();
                }
            }
        }

        return true;
    }

    /**
     * Finds equipment at specific coordinates.
     * @param row the row
     * @param col the column
     * @return the placed equipment at that position, or null if none
     */
    public PlacedEquipment getEquipmentAt(int row, int col) {
        if (row < 0 || row >= MAX_ROWS || col < 0 || col >= MAX_COLS) {
            return null;
        }

        if (grid[row][col] instanceof Cell.Occupied occupied) {
            return occupied.placedEquipment();
        }

        return null;
    }

    /**
     * Adds a new cell to the backpack.
     * @param row the row of the new cell
     * @param col the column of the new cell
     * @return true if cell added successfully
     */
    public boolean addCell(int row, int col) {
        if (row < 0 || row >= MAX_ROWS || col < 0 || col >= MAX_COLS) {
            return false;
        }

        // Check if cell is already available or occupied
        if (!(grid[row][col] instanceof Cell.Blocked)) {
            return false; // Already available or occupied
        }

        // Check if this cell is adjacent to existing backpack
        if (!isAdjacentToBackpack(row, col)) {
            return false;
        }

        // Add the cell
        grid[row][col] = new Cell.Free();

        // Update dimensions if necessary
        if (row >= rows) {
            rows = row + 1;
        }
        if (col >= cols) {
            cols = col + 1;
        }

        return true;
    }

    /**
     * Checks if a position is adjacent to the current backpack.
     * @param row the row
     * @param col the column
     * @return true if adjacent
     */
    private boolean isAdjacentToBackpack(int row, int col) {
        // Check all 4 directions
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int adjRow = row + dir[0];
            int adjCol = col + dir[1];

            if (adjRow >= 0 && adjRow < MAX_ROWS &&
                adjCol >= 0 && adjCol < MAX_COLS &&
                !(grid[adjRow][adjCol] instanceof Cell.Blocked)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets list of cells that can be added to the backpack.
     * @return list of [row, col] pairs
     */
    public List<int[]> getExpandableCells() {
        List<int[]> expandable = new ArrayList<>();

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (grid[i][j] instanceof Cell.Blocked && isAdjacentToBackpack(i, j)) {
                    expandable.add(new int[]{i, j});
                }
            }
        }

        return expandable;
    }

    /**
     * Counts the total number of cells in the backpack.
     * @return number of cells
     */
    public int getTotalCells() {
        int count = 0;
        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                if (!(grid[i][j] instanceof Cell.Blocked)) {
                    count++;
                }
            }
        }
        return count;
    }
}
