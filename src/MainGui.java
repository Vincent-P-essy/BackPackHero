import com.github.forax.zen.Application;
import controller.SimpleGameController;
import model.GameData;
import view.SimpleGameView;
import java.awt.Color;

public class MainGui {
    public static void main(String[] args) {
        Application.run(Color.BLACK, context -> {
            GameData gameData = new GameData();
            SimpleGameView view = new SimpleGameView(context, 1024, 768);
            SimpleGameController controller = new SimpleGameController(gameData, view);
            controller.start();
        });
    }
}
