package model.dungeon;

import model.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the dungeon with multiple floors.
 */
public class Dungeon {
    private final List<Floor> floors;
    private final Hero hero;
    private int currentFloorIndex;

    /**
     * Creates a new dungeon.
     * @param hero the hero
     * @param numberOfFloors number of floors
     */
    public Dungeon(Hero hero, int numberOfFloors) {
        this.hero = Objects.requireNonNull(hero);

        if (numberOfFloors < 1) {
            throw new IllegalArgumentException("At least one floor required");
        }

        this.floors = new ArrayList<>();
        this.currentFloorIndex = 0;

        // Create floors
        for (int i = 0; i < numberOfFloors; i++) {
            floors.add(new Floor(i + 1));
        }
    }

    /**
     * Gets the hero.
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Gets the current floor.
     * @return current floor
     */
    public Floor getCurrentFloor() {
        return floors.get(currentFloorIndex);
    }

    /**
     * Gets the current floor index.
     * @return floor index
     */
    public int getCurrentFloorIndex() {
        return currentFloorIndex;
    }

    /**
     * Gets the total number of floors.
     * @return number of floors
     */
    public int getNumberOfFloors() {
        return floors.size();
    }

    /**
     * Gets a floor by index.
     * @param index the floor index
     * @return the floor
     */
    public Floor getFloor(int index) {
        if (index < 0 || index >= floors.size()) {
            throw new IllegalArgumentException("Invalid floor index");
        }
        return floors.get(index);
    }

    /**
     * Moves to the next floor.
     * @return true if move successful
     */
    public boolean goToNextFloor() {
        if (currentFloorIndex + 1 >= floors.size()) {
            return false; // No more floors
        }

        currentFloorIndex++;
        return true;
    }

    /**
     * Checks if the hero has completed the dungeon.
     * @return true if completed
     */
    public boolean isCompleted() {
        return currentFloorIndex >= floors.size() - 1 &&
               getCurrentFloor().getCurrentRoom().getType() == Room.RoomType.EXIT;
    }
}
