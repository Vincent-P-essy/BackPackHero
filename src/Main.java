import model.GameData;
import view.ConsoleView;
import view.GameView;
import controller.GameController;

/**
 * Main entry point for Backpack Hero game.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== BACKPACK HERO ===");
        System.out.println("Select mode:");
        System.out.println("1. Console Version");
        System.out.println("2. Graphical Version (GUI)");
        System.out.print("Choice (1/2): ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String choice = scanner.nextLine().trim();

        if (choice.equals("2") || choice.equalsIgnoreCase("gui")) {
            System.out.println("Starting GUI...");
            try {
                Class.forName("MainGui").getMethod("main", String[].class).invoke(null, (Object) args);
            } catch (Exception e) {
                System.err.println("GUI not available. Using console version instead.");
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Starting Console Version...");
            // Create game components
            GameData gameData = new GameData();
            GameView gameView = new ConsoleView();
            GameController controller = new GameController(gameData, gameView);

            // Start the game
            controller.start();
        }
    }
}
