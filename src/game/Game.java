package game;

import board.Board;
import board.slot.SlotState;
import models.MovableModel;
import player.CPUPlayer;
import player.Player;

import static game.Input.NONE;

public class Game {
    private boolean gameover = false;

    private Player activePlayer;
    private Player p1;
    private Player p2;
    private Player winner = null;

    private Board board;
    private GameScene scene;

    /**
     * Constructs the Game and allocates coins to players
     * @param scene The scene where the game will take place
     */
    Game(GameScene scene, GameType type, boolean humanFirst) {
        this.scene = scene;

        board = new Board();

        MovableModel[] coinsRed = GameScene.coinsRed;
        MovableModel[] coinsYellow = GameScene.coinsYellow;
        switch (type) {
            case SINGLE:
                if (humanFirst) {
                    p1 = new Player(coinsRed, SlotState.R);
                    p2 = new CPUPlayer(board, coinsYellow, SlotState.Y);
                }else {
                    p1 = new CPUPlayer(board, coinsRed, SlotState.R);
                    p2 = new Player(coinsYellow, SlotState.Y);
                }
                break;
            case MULTIPLAYER:
                p1 = new Player(coinsRed, SlotState.R);
                p2 = new Player(coinsYellow, SlotState.Y);
                break;
            case DEMO:
                p1 = new CPUPlayer(board, coinsRed, SlotState.R);
                p2 = new CPUPlayer(board, coinsYellow, SlotState.Y);
                break;
        }

        activePlayer = p1;
        activePlayer.nextCoin();
    }

    void update() {
        if (gameover) return;
        if (activePlayer.getCurrentCoin().isMoving()) return;

        if (winner != null) {
            scene.drawWinningSpheres(board.getSphereCoords());
            gameover = true;
            return;
        }

        activePlayer.update();

        switch (GameScene.input) {
            case RIGHT:
                activePlayer.moveCoinRight();
                GameScene.input = NONE;
            break;
            case LEFT:
                activePlayer.moveCoinLeft();
                GameScene.input = NONE;
            break;
            case DROP:
                int col = activePlayer.getLastCol();
                //get the first available empty row for the current column
                int row = board.getEmptyRow(col);

                //if column is not full allow it to be played
                if (row != Integer.MAX_VALUE) {
                    if (board.fillSlotWithLines(row, col, activePlayer.getSlotFill()))
                        winner = activePlayer;

                    activePlayer.drop(row);

                    if (winner == null) {
                        //next player
                        if (activePlayer == p1) activePlayer = p2;
                        else activePlayer = p1;


                        if (activePlayer == p1 && activePlayer.getCoin() == GameScene.maxCoins) {
                            //no more coins available - DRAW
                            gameover = true;
                        } else {
                            activePlayer.nextCoin();
                        }
                    }
                }

                GameScene.input = NONE;
                break;
        }
    }
}
