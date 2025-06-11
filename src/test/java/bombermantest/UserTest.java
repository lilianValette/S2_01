package bombermantest;

import com.bomberman.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void testUserConstructorAndGetters() {
        User user = new User("alice", "secret");

        assertEquals("alice", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals(0, user.getGamesPlayed());
        assertEquals(0, user.getGamesWon());
        assertEquals(0, user.getTotalScore());
    }

    @Test
    void testUserFullConstructor() {
        User user = new User("bob", "1234", 10, 4, 1500);

        assertEquals("bob", user.getUsername());
        assertEquals("1234", user.getPassword());
        assertEquals(10, user.getGamesPlayed());
        assertEquals(4, user.getGamesWon());
        assertEquals(1500, user.getTotalScore());
    }

    @Test
    void testIncrementMethods() {
        User user = new User("charlie", "pwd");

        user.incrementGamesPlayed();
        user.incrementGamesPlayed();
        user.incrementGamesWon();
        user.addScore(250);
        user.addScore(100);

        assertEquals(2, user.getGamesPlayed());
        assertEquals(1, user.getGamesWon());
        assertEquals(350, user.getTotalScore());
    }

    @Test
    void testWinRateCalculation() {
        User user = new User("david", "xyz");

        assertEquals(0.0, user.getWinRate());

        user.incrementGamesPlayed();
        user.incrementGamesWon();
        assertEquals(100.0, user.getWinRate());

        user.incrementGamesPlayed();
        assertEquals(50.0, user.getWinRate(), 0.01);
    }

    @Test
    void testToStringAndFromString() {
        User original = new User("emma", "abc", 5, 3, 700);
        String serialized = original.toString();

        assertEquals("emma,abc,5,3,700", serialized);

        User parsed = User.fromString(serialized);
        assertNotNull(parsed);
        assertEquals("emma", parsed.getUsername());
        assertEquals("abc", parsed.getPassword());
        assertEquals(5, parsed.getGamesPlayed());
        assertEquals(3, parsed.getGamesWon());
        assertEquals(700, parsed.getTotalScore());
    }

    @Test
    void testFromStringInvalidFormat() {
        assertNull(User.fromString("invalid,string,not,enough"));
        assertNull(User.fromString(""));
        assertNull(User.fromString(null));
    }
}
