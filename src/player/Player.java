package player;

import board.slot.SlotState;
import game.GameScene;
import models.MovableModel;

public class Player {
    private MovableModel[] coins;
    private int coin = -1;

    //the color assigned to this player
    private SlotState slotFill;

    int lastCol;

    public Player(MovableModel[] coins, SlotState slotFill) {
        this.coins = coins;
        this.slotFill = slotFill;

        //red coins start on the left of the board
        if (slotFill == SlotState.R) lastCol = 0;
        //yellow coins start on the right of the board
        else lastCol = GameScene.columns - 1;
    }

    public MovableModel getCurrentCoin() {
        return coins[coin];
    }

    /**
     * Prepares the next coin to be played
     */
    public void nextCoin() {
        coin++;
        GameScene.moveToBoard(coins[coin], lastCol);
    }

    public void moveCoinRight() {
        if (lastCol < GameScene.columns - 1) {
            GameScene.moveOneColumnRight(coins[coin]);
            lastCol++;
        }
    }

    public void moveCoinLeft() {
        if (lastCol > 0) {
            GameScene.moveOneColumnLeft(coins[coin]);
            lastCol--;
        }
    }

    /**
     * Will subsequently call the game scene's drop
     * @param row The first available row from bottom to top
     */
    public void drop(int row) {
        GameScene.moveCoinDown(coins[coin], row);
    }

    public int getLastCol() {
        return lastCol;
    }

    public SlotState getSlotFill() {
        return slotFill;
    }

    public int getCoin() {
        return coin;
    }

    /**
     * Changes the input to the game scene as necessary.
     */
    public void update() {}
}
