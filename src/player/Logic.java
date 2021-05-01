package player;

import board.Board;
import board.line.Line;
import board.slot.Slot;
import board.slot.SlotState;
import game.GameScene;

import java.util.ArrayList;
import java.util.Random;

/***
 * Base class for the logic of non human players.
 */
abstract class Logic {
    Board board;
    ArrayList<Line> lines;
    Slot[][] slots;

    int maxRow;
    int maxCol;

    SlotState turn;

    Random r = new Random();

    Logic(Board board, SlotState slotFill) {
        this.turn = slotFill;
        this.board = board;
        this.slots = board.slots;
        this.maxCol = GameScene.columns;
        this.maxRow = GameScene.rows;
        lines = board.getLines();
    }

    abstract int chooseColumn();
}
