package bombermantest;
import com.bomberman.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class GameTest {

    private Game game;
    private Grid mockGrid;
    private Level mockLevel;
    private AIDifficulty mockDifficulty;

    @BeforeEach
    public void setUp() {
        mockLevel = mock(Level.class);
        mockDifficulty = mock(AIDifficulty.class);
        game = new Game(13, 11, 1, 0, mockLevel, mockDifficulty);
        mockGrid = game.getGrid(); // Utilise le vrai grid initialisé par Game
    }

    @Test
    public void testGameInitialization() {
        List<Player> players = game.getPlayers();
        assertEquals(1, players.size());
        Player p = players.get(0);
        assertTrue(p.isAlive());
        assertFalse(game.isGameOver());
    }

    @Test
    public void testBombPlacementAndExplosionCell() {
        Player p = game.getPlayers().get(0);
        int px = p.getX(), py = p.getY();

        // Place une bombe
        game.placeBomb(p);
        assertEquals(1, game.getBombs().size());

        // Tick jusqu’à l’explosion
        for (int i = 0; i < Bomb.DEFAULT_TIMER; i++) {
            game.updateBombs();
        }

        // Bombe doit avoir explosé
        assertEquals(0, game.getBombs().size());

        // Cellule centrale de l’explosion
        var explosionCell = game.getExplosionCell(px, py);
        assertNotNull(explosionCell);
        assertEquals(Game.ExplosionPartType.CENTRE, explosionCell.type);
        assertNull(explosionCell.direction);
    }

    @Test
    public void testExplosionDamagesPlayer() {
        // Crée un joueur mock à une position fixe
        Player player = mock(Player.class);
        when(player.getX()).thenReturn(5);
        when(player.getY()).thenReturn(5);
        when(player.isAlive()).thenReturn(true);
        when(player.isInvincibleToBombs()).thenReturn(false);
        doNothing().when(player).takeDamage();

        // Injection manuelle dans la liste des joueurs
        game.getPlayers().clear();
        game.getPlayers().add(player);

        // Place manuellement une bombe à cette position
        Bomb bomb = new Bomb(5, 5, 1, 1, player);
        game.getBombs().add(bomb);
        game.getGrid().setCell(5, 5, Grid.CellType.BOMB);

        // Mise à jour jusqu'à l’explosion
        game.updateBombs(); // tick = 0
        game.updateBombs(); // tick = -1 => explosion

        // Vérifie que takeDamage() a été appelée
        verify(player, atLeastOnce()).takeDamage();
    }

    @Test
    public void testEndGameCondition() {
        List<Player> players = game.getPlayers();
        Player p = players.get(0);

        // Simule sa mort
        p.takeDamage();
        game.updateGameState();

        assertTrue(game.isGameOver());
        assertEquals(p, game.getWinner()); // Il est dernier en vie
    }

    @Test
    public void testGetExplosionCellReturnsNullWhenNotExplosion() {
        assertNull(game.getExplosionCell(0, 0)); // rien n’explose au départ
    }
}

