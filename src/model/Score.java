package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a game score for the Hall of Fame.
 * Score calculation: Max HP + Sum of equipment prices
 */
public class Score implements Comparable<Score> {
    private final String playerName;
    private final int finalScore;
    private final int heroLevel;
    private final int maxHealthPoints;
    private final int equipmentValue;
    private final LocalDateTime dateTime;
    private final boolean victory;

    /**
     * Creates a score entry.
     * @param playerName the player name
     * @param hero the hero at end of game
     * @param victory true if player won
     */
    public Score(String playerName, Hero hero, boolean victory) {
        this.playerName = Objects.requireNonNull(playerName);
        Objects.requireNonNull(hero);

        this.heroLevel = hero.getLevel();
        this.maxHealthPoints = hero.getMaxHealthPoints();
        this.equipmentValue = calculateEquipmentValue(hero);
        this.finalScore = maxHealthPoints + equipmentValue;
        this.dateTime = LocalDateTime.now();
        this.victory = victory;
    }

    /**
     * Creates a score from saved data (for loading).
     */
    public Score(String playerName, int finalScore, int heroLevel, int maxHP,
                 int equipValue, LocalDateTime dateTime, boolean victory) {
        this.playerName = Objects.requireNonNull(playerName);
        this.finalScore = finalScore;
        this.heroLevel = heroLevel;
        this.maxHealthPoints = maxHP;
        this.equipmentValue = equipValue;
        this.dateTime = Objects.requireNonNull(dateTime);
        this.victory = victory;
    }

    /**
     * Calculates total equipment value.
     */
    private int calculateEquipmentValue(Hero hero) {
        int total = 0;

        for (var placedEq : hero.getBackpack().getEquipment()) {
            total += placedEq.equipment().getSellPrice();
        }

        // Add gold
        total += hero.getGold();

        return total;
    }

    /**
     * Gets player name.
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets final score.
     * @return final score
     */
    public int getFinalScore() {
        return finalScore;
    }

    /**
     * Gets hero level.
     * @return hero level
     */
    public int getHeroLevel() {
        return heroLevel;
    }

    /**
     * Gets max health points.
     * @return max HP
     */
    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    /**
     * Gets equipment value.
     * @return equipment value
     */
    public int getEquipmentValue() {
        return equipmentValue;
    }

    /**
     * Gets date/time.
     * @return date time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Checks if this was a victory.
     * @return true if victory
     */
    public boolean isVictory() {
        return victory;
    }

    @Override
    public int compareTo(Score other) {
        // Higher score first
        return Integer.compare(other.finalScore, this.finalScore);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String status = victory ? "VICTORY" : "DEFEAT";

        return String.format("%s - Score: %d | Level: %d | HP: %d | Equipment: %d | %s | %s",
            playerName, finalScore, heroLevel, maxHealthPoints,
            equipmentValue, status, dateTime.format(formatter));
    }

    /**
     * Converts to CSV format for saving.
     * @return CSV string
     */
    public String toCsv() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format("%s,%d,%d,%d,%d,%s,%b",
            playerName, finalScore, heroLevel, maxHealthPoints,
            equipmentValue, dateTime.format(formatter), victory);
    }

    /**
     * Creates Score from CSV string.
     * @param csv CSV string
     * @return Score object
     */
    public static Score fromCsv(String csv) {
        String[] parts = csv.split(",");

        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid CSV format");
        }

        String name = parts[0];
        int score = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);
        int hp = Integer.parseInt(parts[3]);
        int equipValue = Integer.parseInt(parts[4]);
        LocalDateTime dt = LocalDateTime.parse(parts[5]);
        boolean victory = Boolean.parseBoolean(parts[6]);

        return new Score(name, score, level, hp, equipValue, dt, victory);
    }
}
