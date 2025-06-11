package bombermantest;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class PlayerAItest {
    enum CellType { EMPTY, INDESTRUCTIBLE }

    static class Grid {
        private final CellType[][] cells;

        Grid(int width, int height) {
            cells = new CellType[width][height];
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    cells[x][y] = CellType.EMPTY;
        }

        void setCell(int x, int y, CellType type) {
            cells[x][y] = type;
        }

        CellType getCell(int x, int y) {
            return cells[x][y];
        }
    }

    static class Bomb {
        private final int x, y, range, timer;

        Bomb(int x, int y, int range, int timer) {
            this.x = x;
            this.y = y;
            this.range = range;
            this.timer = timer;
        }

        int getX() { return x; }
        int getY() { return y; }
        int getRange() { return range; }
        int getTimer() { return timer; }
    }

    // Champs simulant ceux de la classe testée
    private int lastBombX, lastBombY, lastBombTimer;

    private int dangerLevelAt(int x, int y, List<Bomb> bombs, Grid grid, boolean ignoreOwnLastBomb) {
        int minTick = Integer.MAX_VALUE;
        for (Bomb bomb : bombs) {
            int bx = bomb.getX(), by = bomb.getY(), range = bomb.getRange();
            int timer = bomb.getTimer();
            if (ignoreOwnLastBomb && bx == lastBombX && by == lastBombY && timer == lastBombTimer) continue;
            if (x == bx && y == by) minTick = Math.min(minTick, timer);
            if (y == by && Math.abs(x - bx) <= range) {
                boolean blocked = false;
                for (int ix = Math.min(x, bx) + 1; ix < Math.max(x, bx); ix++) {
                    if (grid.getCell(ix, y) == CellType.INDESTRUCTIBLE) { blocked = true; break; }
                }
                if (!blocked) minTick = Math.min(minTick, timer);
            }
            if (x == bx && Math.abs(y - by) <= range) {
                boolean blocked = false;
                for (int iy = Math.min(y, by) + 1; iy < Math.max(y, by); iy++) {
                    if (grid.getCell(x, iy) == CellType.INDESTRUCTIBLE) { blocked = true; break; }
                }
                if (!blocked) minTick = Math.min(minTick, timer);
            }
        }
        return minTick;
    }

    @Test
    void testDangerLevelSimpleCase() {
        Grid grid = new Grid(5, 5);
        List<Bomb> bombs = List.of(
                new Bomb(2, 2, 2, 5)
        );
        int result = dangerLevelAt(2, 3, bombs, grid, false);
        assertEquals(5, result);
    }

    @Test
    void testDangerLevelBlockedByWall() {
        Grid grid = new Grid(5, 5);
        grid.setCell(2, 2, CellType.INDESTRUCTIBLE); // le bloc le chemin

        List<Bomb> bombs = List.of(
                new Bomb(2, 1, 2, 4)
        );
        int result = dangerLevelAt(2, 3, bombs, grid, false); // chemin blocké
        assertEquals(Integer.MAX_VALUE, result); // aucun danger
    }

    @Test
    void testIgnoreOwnLastBomb() {
        lastBombX = 2;
        lastBombY = 2;
        lastBombTimer = 3;

        Grid grid = new Grid(5, 5);
        List<Bomb> bombs = List.of(
                new Bomb(2, 2, 2, 3)
        );
        int result = dangerLevelAt(2, 2, bombs, grid, true);
        assertEquals(Integer.MAX_VALUE, result);
    }
}
