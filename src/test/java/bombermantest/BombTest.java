package bombermantest;
import com.bomberman.model.Bomb;
import com.bomberman.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BombTest {

    @Test
    void testConstructorWithoutOwner() {
        Bomb bomb = new Bomb(3, 5, 6, 2);

        assertEquals(3, bomb.getX());
        assertEquals(5, bomb.getY());
        assertEquals(6, bomb.getTimer());
        assertEquals(2, bomb.getRange());
        assertNull(bomb.getOwner());
        assertFalse(bomb.isExploded());
    }

    @Test
    void testConstructorWithOwner() {
        Player mockPlayer = new Player(1,0,0,true);
        Bomb bomb = new Bomb(1, 2, 3, 4, mockPlayer);

        assertEquals(1, bomb.getX());
        assertEquals(2, bomb.getY());
        assertEquals(3, bomb.getTimer());
        assertEquals(4, bomb.getRange());
        assertEquals(mockPlayer, bomb.getOwner());
    }

    @Test
    void testTickingAndExplosion() {
        Bomb bomb = new Bomb(0, 0, 2, 1);

        assertFalse(bomb.isExploded());
        bomb.tick(); // timer = 1
        assertFalse(bomb.isExploded());
        bomb.tick(); // timer = 0
        assertTrue(bomb.isExploded());
    }
}
