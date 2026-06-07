package view;

import model.GameData;

/**
 * Interface for game views.
 */
public interface GameView {
    /**
     * Displays the game.
     * @param gameData the game data to display
     */
    void display(GameData gameData);

    /**
     * Gets the width of the view.
     * @return width in pixels
     */
    int getWidth();

    /**
     * Gets the height of the view.
     * @return height in pixels
     */
    int getHeight();
}
