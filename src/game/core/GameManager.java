package game.core;

import javax.swing.Timer;

import game.actor.Actor;
import game.actor.Bird;
import game.actor.Cat;
import game.actor.Dog;
import game.items.Berry;
import game.items.Bone;
import game.items.Fish;
import ui.EventBoard;
import game.score.ScoreSpecificator;
import ui.HUD;
import weather.state.WeatherStateBehavior;

import java.util.List;

public class GameManager {
    private final Game game;
    private final HUD hud;
    private static final int TURN_MS = 900;
    private Timer turnTimer;
    private boolean waitingForPlayer = false;
    private final List<ScoreSpecificator<?, ?>> SCORE_SPECS = List
            .of(new ScoreSpecificator<Dog, Bone>(Dog.class, Bone.class, "bones") {
            }, new ScoreSpecificator<Cat, Fish>(Cat.class, Fish.class, "fish") {
            }, new ScoreSpecificator<Bird, Berry>(Bird.class, Berry.class, "berries") {
            });
    private final EventBoard eventLog = new EventBoard(5);
    private static final int ANNOUNCE_DELAY_MS = 2000;
    private static final int AFTER_ACTORS_MS = 800;
    private static final int COUNTDOWN_STEP_MS = 1000;
    private boolean gameOver = false;

    public GameManager(Game game, HUD hud) {
        this.game = game;
        this.hud = hud;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void endGame(String message) {
        gameOver = true;
        waitingForPlayer = false;
        if (turnTimer != null)
            turnTimer.stop();
        hud.show(message);
    }

    private void checkWinAndMaybeEnd(Actor a) {
        boolean allResting = game.stage().getActors().stream()
                .allMatch(Actor::isResting);
        if (allResting) {
            endGame("PETS are tired! Press R to restart.");
        }
        if (a.bag().size() >= a.bag().capacity()) {
            String winner = a.isChosen() ? "PLAYER" : ("BOT " + a.getActorName());
            endGame(winner + " WINS! Press R to restart");
        }
    }

    public void restartGame() {
        game.stage().stopWeatherClient();
        gameOver = false;
        waitingForPlayer = false;
        if (turnTimer != null)
            turnTimer.stop();
        hud.clear();
        eventLog.clear();
        game.stage().getWeatherEffectsBoard().clear();
        game.clearTurnOrder();
        game.stage().reset();
        startIntro();
    }

    private void pickRandomActor() {
        List<Class<? extends Actor>> pool = List.of(Cat.class, Dog.class, Bird.class);
        Class<? extends Actor> chosen = pool.get(game.random().nextInt(pool.size()));
        game.setSelected(chosen);

        for (Actor a : game.stage().getActors()) {
            if (a.getClass() == chosen) {
                a.actorIsChosen();
                break;
            }
        }
    }

    private void doOneTurn() {
        Actor a = game.currentActor();
        if (a == null) {
            return;
        }
        if (a.isResting()) {
            game.nextTurn();
            return;
        }
        if (a.isSkippingTurn()) {
            a.reduceSkipCounter();
            eventLog.add(a.getActorName() + " skips this turn", a.isChosen());
            game.nextTurn();
            return;
        }
        if (a.isChosen()) {
            waitingForPlayer = true;
            if (turnTimer != null)
                turnTimer.stop();
            hud.show("Your move (use arrows)!");
            return;
        }
        Cell before = a.getLocation();
        boolean moved = a.takeTurn(game);
        if (!moved) {
            hud.show("BOT " + a.getActorName() + ": TURN LOST! INVALID MOVE");
            schedule(() -> {
                game.nextTurn();
                hud.clear();
            }, 800);
            return;
        }
        Cell after = a.getLocation();
        if (after != null && after != before) {
            String tileMsg = game.stage().applyTileEffects(a);
            if (tileMsg != null) {
                eventLog.add(tileMsg, a.isChosen());
            }
            handleWeatherReaction(a, after);
        }
        checkWinAndMaybeEnd(a);
        if (gameOver) {
            return;
        }
        game.nextTurn();
    }

    public void startTurns() {
        if (game.getTurnOrder().isEmpty())
            game.initTurnOrder();
        if (turnTimer != null)
            turnTimer.stop();

        turnTimer = new Timer(TURN_MS, e -> doOneTurn());
        turnTimer.setRepeats(true);
        turnTimer.start();
        System.out.println("TURN TIMER STARTED");
    }

    public void playerMoveNow() {
        if (!waitingForPlayer) {
            return;
        }

        Actor a = game.currentActor();
        if (a == null || !a.isChosen()) {
            return;
        }

        boolean validMove = a.takeTurn(game);
        if (!validMove) {
            hud.show("PLAYER: TURN LOST! INVALID MOVE!");
            waitingForPlayer = false;
            schedule(() -> {
                game.nextTurn();
                hud.clear();
                if (turnTimer != null)
                    turnTimer.start();
            }, 800);
            return;
        }
        String tileMsg = game.stage().applyTileEffects(a);
        if (tileMsg != null) {
            eventLog.add(tileMsg, a.isChosen());
        }

        Cell cell = a.getLocation();
        if (cell != null) {
            handleWeatherReaction(a, cell);
        }
        checkWinAndMaybeEnd(a);
        if (gameOver) {
            return;
        }
        waitingForPlayer = false;
        game.nextTurn();
        hud.clear();
        if (turnTimer != null) {
            turnTimer.start();
        }
    }

    public boolean isWaitingForPlayer() {
        return waitingForPlayer;
    }

    public void startIntro() {
        hud.show("Randomly choosing your pet…");
        pickRandomActor();

        schedule(() -> {
            String name = game.getSelected().getSimpleName();
            hud.show("Your pet is: " + name);

            schedule(() -> countdown("Placing pets on the map", () -> {
                game.stage().placeActors(game.random(), game.getSelected());
                hud.clear();

                schedule(() -> countdown("Placing items on the map", () -> {
                    game.stage().placeItems(game.random());
                    hud.show("The game has started!");

                    game.stage().startWeatherClient();

                    schedule(() -> {
                        hud.clear();
                        game.initTurnOrder();
                        startTurns();
                    }, 2000);
                }), AFTER_ACTORS_MS);
            }), 1200);
        }, ANNOUNCE_DELAY_MS);
    }

    public List<Actor> getActors() {
        return game.stage().getActors();
    }

    public List<ScoreSpecificator<?, ?>> getScoreSpecs() {
        return SCORE_SPECS;
    }

    public EventBoard getEventBoard() {
        return eventLog;
    }

    private void countdown(final String prefix, final Runnable onDone) {
        hud.show(prefix + " 3…");
        final int[] step = { 0 };

        Timer t = new Timer(COUNTDOWN_STEP_MS, e -> {
            switch (step[0]) {
                case 0 -> {
                    hud.show(prefix + " 3…2…");
                    step[0] = 1;
                }
                case 1 -> {
                    hud.show(prefix + " 3…2…1");
                    step[0] = 2;
                }
                default -> {
                    ((Timer) e.getSource()).stop();
                    onDone.run();
                }
            }
        });
        t.start();
    }

    private void schedule(final Runnable action, int delayMs) {
        Timer t = new Timer(delayMs, e -> {
            ((Timer) e.getSource()).stop();
            action.run();
        });
        t.setRepeats(false);
        t.start();
    }

    private void handleWeatherReaction(Actor a, Cell cell) {
        if (cell == null)
            return;
        WeatherStateBehavior weather = cell.getWeatherState();
        if (weather == null)
            return;
        String msg = a.reactToWeather(weather, cell.getTerrain());
        if (msg != null && !msg.isEmpty()) {
            game.stage().getWeatherEffectsBoard().add(msg, a.isChosen());
        }
    }
}
