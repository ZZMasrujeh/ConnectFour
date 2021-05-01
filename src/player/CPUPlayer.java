package player;

import board.Board;
import board.slot.SlotState;
import game.GameScene;
import game.Input;
import models.MovableModel;

public class CPUPlayer extends Player {

    private Logic logic;
    private int chosenColumn;

    /**
     * Creates a non human player object.
     * @param board The board to "read" and play on
     * @param coins coins of this player
     * @param slotFill color of this player
     */
    public CPUPlayer(Board board, MovableModel[] coins, SlotState slotFill) {
        super(coins, slotFill);

        switch (GameScene.difficulty) {
            case EASY: logic = new LogicEasy(board, slotFill); break;
            case HARD: logic = new LogicHard(board, slotFill); break;
            case EXPERT: logic = new LogicExpert(board, slotFill); break;

        }
//        if (easy) {
//            logic = new LogicEasy(board, slotFill);
//        } else {
//            logic = new LogicHard(board, slotFill);
//        }
    }

    @Override
    public void update() {
        //coin must be moved toward the left of the board
        if (chosenColumn < lastCol) {
            GameScene.input = Input.LEFT;
            return;
        }

        //coin must be moved toward the right of the board
        if (chosenColumn == lastCol) {
            GameScene.input = Input.DROP;
            return;
        }

        //coin is above the correct column
        GameScene.input = Input.RIGHT;
    }

    @Override
    public void nextCoin() {
        super.nextCoin();

        chosenColumn = logic.chooseColumn();
//        System.out.println("Thinking "  + chosenColumn);
    }
}
