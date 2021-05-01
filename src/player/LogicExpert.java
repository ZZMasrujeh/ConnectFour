package player;

import board.Board;
import board.line.Line;
import board.line.LineState;
import board.slot.Slot;
import board.slot.SlotState;

import java.util.ArrayList;
import java.util.Arrays;

public class LogicExpert extends Logic {
    private SlotState opponentTurn;
    private SlotState turn;
    private Slot[][] slots;

    LogicExpert(Board board, SlotState slotFill) {
        super(board, slotFill);

        this.turn = slotFill;
        opponentTurn = turn == SlotState.R ? SlotState.Y : SlotState.R;
        this.slots = board.slots;
    }

    private boolean fillCenter() {
        return slots[1][maxCol / 2].isEmpty();
    }
    private void printSlots() {
        System.out.println();
        for (Slot[] slot : slots) {
            for (Slot value : slot) {
                System.out.print(value.toString() + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }
    private void linesOf2And3(int[] count2, int[] count3, SlotState turn) {
//        printSlots();
        ArrayList<Line> lines = board.getLines();
        int c2 = 0;
        int c3 = 0;
        for (Line line : lines) {
            LineState lineState = line.getLineState();
            if (turn == line.getOriginalSlotState()) {
                continue;
            }
            if (lineState == LineState.TWO) {
                c2++;
            }
            if (lineState == LineState.THREE) {
                c3++;
            }
        }
        count2[0] = c2;
        count3[0] = c3;
    }


    int maxDepth = 5;

    int go(int depth, int tile, int[] a, int[] b, SlotState turn) {
        int[] count2 = {0};
        int[] count3 = {0};
        if (this.turn == turn) {
            //maximizing player
            if (depth > maxDepth || tile > maxCol * maxRow) {
                linesOf2And3(count2, count3, turn);
                int result =  count2[0] * 2 + count3[0] * 5;
                return result;
            }
            int v = Integer.MIN_VALUE;
            for (int c = 0; c < maxCol; c++) {
                for (int r = maxRow - 1; r >= 0; r--) {
                    if (board.slots[r][c].isEmpty()) {
                        boolean win = board.fillSlot(r, c, turn);
                        if (win) {
                            board.slots[r][c].resetState();
                            return 1000;
                        }
                        int childValue = go(depth + 1, tile + 1, a, b, opponentTurn);
                        if (childValue > v) v = childValue;
                        if (childValue >= b[0]) {
                            board.slots[r][c].resetState();
                            return v;
                        }
                        if (childValue > a[0]) a[0] = childValue;
                        board.slots[r][c].resetState();
                        break;
                    }
                }
            }
            return v;
        }else {
            //minimizing player
            int v = Integer.MAX_VALUE;
            if (depth > maxDepth || tile > maxCol * maxRow) {
                linesOf2And3(count2, count3, turn);
                return (count2[0] * (-2)) + (count3[0] * (-5));
            }
            for (int c = 0; c < maxCol; c++) {
                for (int r = maxRow - 1; r >= 0; r--) {
                    if (board.slots[r][c].isEmpty()) {
                        boolean win = board.fillSlot(r, c, turn);
                        if (win) {
                            board.slots[r][c].resetState();
                            return -100;
                        }
                        int childValue = go(depth + 1, tile + 1, a, b, this.turn);
                        if (childValue < v) v = childValue;
                        if (childValue <= a[0]) {
                            board.slots[r][c].resetState();
                            return v;
                        }
                        if (childValue < b[0]) b[0] = childValue;
                        board.slots[r][c].resetState();
                        break;
                    }
                }
            }
            return v;
        }
    }

    private int[] firstRun(int count) {
        int[] choices = new int[maxCol];
        for (int c = 0; c < maxCol; c++) {
            if (!board.slots[0][c].isEmpty()) {
                choices[c] = Integer.MIN_VALUE;
                continue;
            }

            int[] a ={Integer.MIN_VALUE};
            int[] b = {Integer.MAX_VALUE};
            for (int r = maxRow - 1; r >= 0; r--) {
                if (board.slots[r][c].isEmpty()) {
                    if (board.fillSlot(r, c, this.turn)) {
                        choices[c] = 1000;
                    }else {
                        choices[c] = go(1, count + 1, a, b, opponentTurn);
                    }
                    board.slots[r][c].resetState();
                    break;
                }
            }
        }
        return choices;
    }


    @Override
    public int chooseColumn() {
        if (fillCenter()) return maxCol / 2;

        int count = 0;
        SlotState[][] states = new SlotState[maxRow][maxCol];
        for (int r = maxRow - 1; r >= 0; r--) {
            for (int c = 0; c < states[r].length; c++) {
                SlotState s = slots[r][c].getState();
                states[r][c] = s;
                if (s != SlotState.E)
                    count++;
            }
        }

        int[] choices = firstRun(count);
        System.out.println(Arrays.toString(choices));

        int max = Integer.MIN_VALUE;
        int index = 0;

        for (int i = 0; i < choices.length; i++) {
            if (choices[i] > max) {
                max = choices[i];
                index = i;
            }
        }

        return index;
    }
}
