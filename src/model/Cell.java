package model;

import model.equipment.Equipment;

/**
 * Represents a cell in the backpack grid.
 * Can be in one of three states: Blocked, Free, or occupied by PlacedEquipment.
 */
public sealed interface Cell permits Cell.Blocked, Cell.Free, Cell.Occupied {

    /**
     * Represents a blocked cell that cannot be used.
     */
    record Blocked() implements Cell {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public PlacedEquipment getPlacedEquipment() {
            return null;
        }
    }

    /**
     * Represents a free cell that can be used for placing equipment.
     */
    record Free() implements Cell {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public PlacedEquipment getPlacedEquipment() {
            return null;
        }
    }

    /**
     * Represents a cell occupied by equipment.
     */
    record Occupied(PlacedEquipment placedEquipment) implements Cell {
        public Occupied {
            if (placedEquipment == null) {
                throw new IllegalArgumentException("PlacedEquipment cannot be null");
            }
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public PlacedEquipment getPlacedEquipment() {
            return placedEquipment;
        }
    }

    /**
     * Checks if this cell is available for placing equipment.
     * @return true if the cell is free
     */
    boolean isAvailable();

    /**
     * Checks if this cell is occupied by equipment.
     * @return true if the cell contains equipment
     */
    boolean isOccupied();

    /**
     * Gets the placed equipment in this cell, if any.
     * @return the placed equipment, or null if the cell is not occupied
     */
    PlacedEquipment getPlacedEquipment();

    /**
     * Represents an equipment placed in the backpack.
     */
    record PlacedEquipment(Equipment equipment, int row, int col, int rotation) {
        public PlacedEquipment {
            if (equipment == null) {
                throw new IllegalArgumentException("Equipment cannot be null");
            }
            if (row < 0 || col < 0) {
                throw new IllegalArgumentException("Row and column must be non-negative");
            }
            if (rotation < 0 || rotation % 90 != 0) {
                throw new IllegalArgumentException("Rotation must be a multiple of 90");
            }
        }
    }
}
