import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

import weather.WeatherManager;
import game.core.Stage;
import game.core.Game;
import game.core.Grid;
import game.core.Direction;
import game.core.GameManager;
import game.network.Client;
import game.network.MockWeatherServer;
import ui.WeatherForecastBoard;
import ui.WeatherEffectsBoard;
import ui.ScoreBoard;
import ui.CuriosityBoard;
import ui.HUD;

public class Main extends JFrame {

    public static void main(String[] args) throws Exception {
        MockWeatherServer mockServer = new MockWeatherServer(8080);
        mockServer.start();
        new Main();
    }

    class Canvas extends JPanel {
        WeatherManager weatherManager;
        Stage stage;
        private final Game game;
        private final HUD hud;
        Grid grid;
        Client client;
        GameManager manager;
        private final WeatherForecastBoard forecastBoard;
        private final WeatherEffectsBoard weatherEffectsBoard;

        private final Map<Integer, Direction> keyDirectionMap = Map.of(
                KeyEvent.VK_UP, Direction.UP,
                KeyEvent.VK_DOWN, Direction.DOWN,
                KeyEvent.VK_LEFT, Direction.LEFT,
                KeyEvent.VK_RIGHT, Direction.RIGHT);

        public Canvas() {
            setPreferredSize(new Dimension(1350, 720));
            setFocusable(true);
            weatherManager = new WeatherManager();
            stage = new Stage(weatherManager);
            game = new Game(stage);
            hud = new HUD();
            manager = new GameManager(game, hud);
            manager.startIntro();
            forecastBoard = new WeatherForecastBoard(0, 0, 0, 0);
            weatherManager.registerObserver(forecastBoard);
            weatherEffectsBoard = new WeatherEffectsBoard(6, 0, 0, 0, 0);
            stage.attachWeatherEffectsBoard(weatherEffectsBoard);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();

                    if (code == KeyEvent.VK_R && manager.isGameOver()) {
                        manager.restartGame();
                        forecastBoard.clear();
                        return;
                    }

                    Optional.ofNullable(keyDirectionMap.get(code))
                            .ifPresent(dir -> {
                                game.setPlayerDir(dir);
                                if (manager.isWaitingForPlayer()) {
                                    manager.playerMoveNow();
                                }
                            });
                }
            });
            Timer repaintTimer = new Timer(16, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            repaintTimer.start();
        }

        @Override
        public void paint(Graphics g) {
            Point mouse = getMousePosition();
            stage.setMousePos(mouse);
            stage.paint(g);

            int panelW = 295;
            int gap = 15;
            int leftX = getWidth() - (panelW * 2) - gap * 2 - 10;
            int rightX = leftX + panelW + gap;
            int weatherY = 170;
            int petsY1 = 430;
            int petsY2 = 570;
            int eventsY1 = 430;
            int eventsY2 = 570;

            int weatherW = (panelW * 2) + gap * 2;
            forecastBoard.setLayout(leftX, weatherY, weatherW, getHeight());
            forecastBoard.paint(g);
            CuriosityBoard curiosityBoard = new CuriosityBoard(leftX, petsY1, panelW, getHeight(), manager.getActors());

            ScoreBoard scoreBoard = new ScoreBoard(manager.getActors(), manager.getScoreSpecs(), leftX, petsY2, panelW,
                    getHeight());
            curiosityBoard.paint(g);
            scoreBoard.paint(g);

            manager.getEventBoard().setLayout(rightX, eventsY1, panelW, getHeight());
            manager.getEventBoard().paint(g);
            weatherEffectsBoard.setLayout(rightX, eventsY2, panelW, getHeight());
            weatherEffectsBoard.paint(g);

            hud.paint(g, getWidth(), getHeight(), Color.RED);

            g.setColor(new Color(50, 50, 50));
            g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
            g.drawString("WEATHER SECTION", leftX + 10, weatherY - 10);
            g.drawString("PETS SECTION", leftX + 10, petsY1 - 15);
            g.drawString("EVENTS SECTION", rightX + 10, eventsY1 - 15);

        }
    }

    private Main() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas canvas = new Canvas();
        this.setContentPane(canvas);
        this.pack();
        this.setVisible(true);
    }
}
