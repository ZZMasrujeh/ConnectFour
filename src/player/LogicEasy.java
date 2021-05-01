package player;

import board.Board;
import board.slot.SlotState;

import java.util.ArrayList;

/**
 * Logic that chooses randomly.
 */
public class LogicEasy extends Logic {

    LogicEasy(Board board, SlotState slotFill) {
        super(board, slotFill);
    }

    @Override
    public int chooseColumn() {
        ArrayList<Integer> availableColumns = new ArrayList<>();
        for (int c = 0; c < maxCol; c++) {
            if (slots[0][c].isEmpty()) {
                availableColumns.add(c);
            }
        }
        return availableColumns.get(r.nextInt(availableColumns.size()));
    }
}
