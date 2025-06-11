package bombermantest;
import com.bomberman.model.Bomb;
import com.bomberman.model.Grid;
import com.bomberman.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;
    private Grid grid;

    @BeforeEach
    void setUp() {
        player = new Player(1, 1, 1, true);
        grid = new Grid(5, 5, null);
    }

    @Test
    void testInitialValues() {
        assertEquals(1, player.getId());
        assertEquals(1, player.getX());
        assertEquals(1, player.getY());
        assertTrue(player.isAlive());
        assertTrue(player.isHuman());
        assertEquals(3, player.getLives());
        assertFalse(player.isAI());
    }

    @Test
    void testMovement() {
        grid.setCell(2, 1, Grid.CellType.EMPTY);
        player.moveRight(grid);
        assertEquals(2, player.getX());
        assertEquals(1, player.getY());

        grid.setCell(2, 2, Grid.CellType.EMPTY);
        player.moveDown(grid);
        assertEquals(2, player.getX());
        assertEquals(2, player.getY());
    }

    @Test
    void testTakeDamageAndKill() {
        player.takeDamage(); // lives: 2
        assertTrue(player.isAlive());
        player.takeDamage(); // lives: 1
        player.takeDamage(); // lives: 0
        assertFalse(player.isAlive());
        assertEquals(0, player.getLives());
    }

    @Test
    void testKill() {
        player.kill();
        assertFalse(player.isAlive());
    }

    @Test
    void testAddLife() {
        player.addLife();
        assertEquals(4, player.getLives());
    }

    @Test
    void testDropBomb() {
        List<Bomb> bombs = new ArrayList<>();
        Bomb bomb = player.dropBomb(5, bombs);
        assertNotNull(bomb);
        assertEquals(player.getX(), bomb.getX());
        assertEquals(player.getY(), bomb.getY());
    }

    @Test
    void testDropBombLimit() {
        List<Bomb> bombs = new ArrayList<>();
        bombs.add(new Bomb(1, 1, 5, 1));
        // maxBombs = 1
        Bomb bomb = player.dropBomb(5, bombs);
        assertNull(bomb);
    }

    @Test
    void testAddJacketBonusTempAndInvincibility() {
        assertFalse(player.isInvincibleToBombs());
        player.addJacketBonusTemp(5);
        assertTrue(player.isInvincibleToBombs());
    }

    @Test
    void testAddFlameBonusAndUpdate() {
        int initialRange = player.getBombRange();
        player.addFlameBonusTemp(2, 1.0);
        assertEquals(initialRange + 2, player.getBombRange());

        // Simule l'expiration du bonus
        for (int i = 0; i < 3; i++) player.updateActiveBonuses();

        assertEquals(initialRange, player.getBombRange());
    }
}
