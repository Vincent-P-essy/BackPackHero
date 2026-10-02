import com.github.forax.zen.Application;
import controller.SimpleGameController;
import model.GameData;
import model.dungeon.Room;
import view.SimpleGameView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Plays the original GUI with keyboard and mouse; model access is read-only. */
public class GameplayCapture {
    static Robot robot;
    static void key(int code) throws Exception {
        robot.keyPress(code); robot.delay(90); robot.keyRelease(code); Thread.sleep(450);
    }
    static void capture(String name) throws Exception {
        ImageIO.write(robot.createScreenCapture(new Rectangle(0, 0, 1024, 768)), "png",
            Path.of("docs/screenshots/" + name + ".png").toFile());
    }
    static void play(GameData data) {
        try {
            robot = new Robot();
            Thread.sleep(2000);
            EventQueue.invokeAndWait(() -> {
                for (Frame frame : Frame.getFrames()) if (frame.isVisible()) { frame.toFront(); frame.requestFocus(); }
            });
            var floor = data.getDungeon().getCurrentFloor();
            Room start = floor.getCurrentRoom(), target = null;
            Map<Room, Room> parent = new HashMap<>();
            ArrayDeque<Room> queue = new ArrayDeque<>();
            parent.put(start, null); queue.add(start);
            while (!queue.isEmpty()) {
                Room current = queue.remove();
                if (current.getType() == Room.RoomType.ENEMY) { target = current; break; }
                for (int[] delta : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                    Room next = floor.getRoom(current.getRow()+delta[0], current.getCol()+delta[1]);
                    if (next != null && !parent.containsKey(next)) { parent.put(next,current); queue.add(next); }
                }
            }
            if (target == null) throw new AssertionError("No reachable enemy");
            var route = new ArrayList<Room>();
            for (Room current=target; current!=start; current=parent.get(current)) route.add(current);
            Collections.reverse(route);
            int walked = 0;
            for (Room next : route) {
                if (data.getState() != GameData.GameState.EXPLORING) key(KeyEvent.VK_SPACE);
                Room here = floor.getCurrentRoom();
                key(next.getRow()>here.getRow()?KeyEvent.VK_DOWN:next.getRow()<here.getRow()?KeyEvent.VK_UP:
                    next.getCol()>here.getCol()?KeyEvent.VK_RIGHT:KeyEvent.VK_LEFT);
                if (floor.getCurrentRoom()!=next) throw new AssertionError("Arrow key did not move hero");
                walked++;
            }
            if (data.getState()!=GameData.GameState.COMBAT) throw new AssertionError("Combat did not begin");
            var enemy = data.getCurrentCombat().getEnemies().getFirst();
            int before = enemy.getCurrentHealthPoints();
            robot.mouseMove(670,390); robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(90); robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); Thread.sleep(500);
            int after = enemy.getCurrentHealthPoints();
            if (after != before-1) throw new AssertionError("Mouse attack did not damage enemy");
            capture("gameplay-attack");
            key(KeyEvent.VK_E);
            if (data.getHero().getEnergy()!=3) throw new AssertionError("End turn did not restore energy");
            capture("gameplay");
            String report = "{\"played\":true,\"interface\":\"GUI\",\"java\":21,\"walked_rooms\":"+walked+
                ",\"enemy_hp_before\":"+before+",\"enemy_hp_after_attack\":"+after+
                ",\"actions\":[\"arrow movement\",\"mouse attack\",\"end turn\"]}";
            Files.writeString(Path.of("docs/gameplay.json"), report);
            key(KeyEvent.VK_Q);
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
    }
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("docs/screenshots"));
        Application.run(Color.BLACK, context -> {
            GameData data = new GameData();
            SimpleGameView view = new SimpleGameView(context,1024,768);
            new Thread(() -> play(data)).start();
            new SimpleGameController(data,view).start();
        });
    }
}
