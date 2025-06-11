package bombermantest;
import com.bomberman.model.Level;
import com.bomberman.model.Grid;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GridTest {


    @Test
    public void testGridInitializationWithLevel() {
        int[][] layout = {
                {1, 0, 2},
                {0, 2, 1},
                {2, 1, 0}
        };
        Level level = new Level("","","","",layout);
        Grid grid = new Grid(3, 3, level);

        assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(0, 0));
        assertEquals(Grid.CellType.EMPTY, grid.getCell(1, 0));
        assertEquals(Grid.CellType.DESTRUCTIBLE, grid.getCell(2, 0));
        assertEquals(Grid.CellType.EMPTY, grid.getCell(0, 1));
        assertEquals(Grid.CellType.DESTRUCTIBLE, grid.getCell(1, 1));
        assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(2, 1));
        assertEquals(Grid.CellType.DESTRUCTIBLE, grid.getCell(0, 2));
        assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(1, 2));
        assertEquals(Grid.CellType.EMPTY, grid.getCell(2, 2));
    }

    @Test
    public void testGridInitializationWithoutLevel() {
        Grid grid = new Grid(5, 5, null);

        // Bords doivent être INDESTRUCTIBLE
        for (int i = 0; i < 5; i++) {
            assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(i, 0));
            assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(i, 4));
            assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(0, i));
            assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(4, i));
        }

        // Centre en (2,2) doit être INDESTRUCTIBLE (car pair, pair)
        assertEquals(Grid.CellType.INDESTRUCTIBLE, grid.getCell(2, 2));
    }

    @Test
    public void testSetAndGetCell() {
        Grid grid = new Grid(3, 3, null);

        grid.setCell(1, 1, Grid.CellType.EXPLOSION);
        assertEquals(Grid.CellType.EXPLOSION, grid.getCell(1, 1));

        // Hors limites
        assertNull(grid.getCell(-1, 0));
        assertNull(grid.getCell(3, 3));
    }

    @Test
    public void testIsInBounds() {
        Grid grid = new Grid(3, 3, null);

        assertTrue(grid.isInBounds(0, 0));
        assertTrue(grid.isInBounds(2, 2));
        assertFalse(grid.isInBounds(-1, 0));
        assertFalse(grid.isInBounds(3, 3));
    }
}
