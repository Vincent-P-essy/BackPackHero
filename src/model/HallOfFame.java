package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manages the Hall of Fame - top scores.
 */
public class HallOfFame {
    private static final String SAVE_FILE = "hall_of_fame.csv";
    private static final int MAX_ENTRIES = 10;

    private final List<Score> scores;

    /**
     * Creates a Hall of Fame.
     */
    public HallOfFame() {
        this.scores = new ArrayList<>();
        load();
    }

    /**
     * Adds a score to the Hall of Fame.
     * @param score the score to add
     */
    public void addScore(Score score) {
        Objects.requireNonNull(score);

        scores.add(score);
        Collections.sort(scores);

        // Keep only top MAX_ENTRIES
        while (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }

        save();
    }

    /**
     * Gets all scores.
     * @return list of scores (sorted)
     */
    public List<Score> getScores() {
        return new ArrayList<>(scores);
    }

    /**
     * Gets top N scores.
     * @param n number of scores
     * @return list of top scores
     */
    public List<Score> getTopScores(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N must be positive");
        }

        int limit = Math.min(n, scores.size());
        return new ArrayList<>(scores.subList(0, limit));
    }

    /**
     * Checks if a score would make it into the Hall of Fame.
     * @param score the score to check
     * @return true if it would be in top MAX_ENTRIES
     */
    public boolean isHighScore(Score score) {
        Objects.requireNonNull(score);

        if (scores.size() < MAX_ENTRIES) {
            return true;
        }

        return score.getFinalScore() > scores.get(scores.size() - 1).getFinalScore();
    }

    /**
     * Gets the rank of a score.
     * @param score the score
     * @return rank (1-based), or -1 if not in Hall of Fame
     */
    public int getRank(Score score) {
        Objects.requireNonNull(score);

        int rank = scores.indexOf(score);
        return rank >= 0 ? rank + 1 : -1;
    }

    /**
     * Clears all scores.
     */
    public void clear() {
        scores.clear();
        save();
    }

    /**
     * Saves scores to file.
     */
    private void save() {
        try {
            Path path = Paths.get(SAVE_FILE);
            List<String> lines = new ArrayList<>();

            for (Score score : scores) {
                lines.add(score.toCsv());
            }

            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Error saving Hall of Fame: " + e.getMessage());
        }
    }

    /**
     * Loads scores from file.
     */
    private void load() {
        try {
            Path path = Paths.get(SAVE_FILE);

            if (!Files.exists(path)) {
                return; // No save file yet
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    try {
                        scores.add(Score.fromCsv(line));
                    } catch (Exception e) {
                        System.err.println("Error parsing score line: " + line);
                    }
                }
            }

            Collections.sort(scores);
        } catch (IOException e) {
            System.err.println("Error loading Hall of Fame: " + e.getMessage());
        }
    }

    /**
     * Displays the Hall of Fame.
     */
    public void display() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                           HALL OF FAME");
        System.out.println("=".repeat(80));

        if (scores.isEmpty()) {
            System.out.println("No scores yet. Be the first hero!");
        } else {
            System.out.println();
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                String medal = "";

                if (i == 0) medal = "🥇";
                else if (i == 1) medal = "🥈";
                else if (i == 2) medal = "🥉";

                System.out.printf("%2d. %s %s%n", i + 1, medal, score);
            }
        }

        System.out.println("=".repeat(80));
    }
}
