package player;

import board.*;
import board.line.*;
import board.slot.Slot;
import board.slot.SlotState;

import java.util.ArrayList;

import static board.line.LineState.*;

/**
 * Logic that is good enough for a challenging game, BUT NOT perfect.
 */
public class LogicHard extends Logic {

   Slot[][] slots;
   LogicHard(Board board, SlotState slotFill) {
       super(board, slotFill);

       this.slots = board.slots;
   }

    public int chooseColumn() {
        printLines();
        readLines();
        checkLineIntersections();
        printValues();
        printBestOfCollumn();
        printSlots();
        return bestColumn();
    }

    /**
     * Assign values to slots of every line
     */
    private void readLines() {
        for (Line line : lines) {
            LineState lineState = line.getLineState();
            if (lineState == BLOCKED) continue;
            if (lineState == EMPTY) continue;

            LineOrientation lineOrientation = line.getLineOrientation();
            Slot[] slotsArray = line.getSlotsArray();
            ArrayList<Integer> indices = line.getIndices();

            boolean myLine = line.getOriginalSlotState() == turn;
            switch (lineState) {
                case TWO:
                    Slot beforeZero = null;
                    Slot afterThree = null;
                    if (lineOrientation == LineOrientation.HORIZONTAL) {
                        beforeZero = slotsArray[0].getLeft();
                        afterThree = slotsArray[3].getRight();
                    }
                    if (lineOrientation == LineOrientation.DIAGONAL_LEFT) {
                        beforeZero = slotsArray[0].getTopRight();
                        afterThree = slotsArray[3].getBottomLeft();
                    }
                    if (lineOrientation == LineOrientation.DIAGONAL_RIGHT) {
                        beforeZero = slotsArray[0].getTopLeft();
                    }
                    if (beforeZero != null && afterThree != null) {
                        traps(beforeZero, afterThree, line);
                    }
                    break;

                case THREE:
                    int winValue = myLine ? Values.WIN : Values.WIN_OPPONNENT;
                    int loseValue = myLine ? Values.WIN_DONT_PLAY : Values.WIN_OPPONENT_DONT_PLAY;
                    setValue(slotsArray[indices.get(0)], winValue);
                    Slot bottom = slotsArray[indices.get(0)].getBottom();
                    if (bottom != null && bottom.isAvailable())
                        setValue(bottom, loseValue);
                    break;
            }
        }
    }

    /**
     * Assigns values for traps that may occur between two lines
     */
    private void checkLineIntersections() {
        ArrayList<LineIntersection> intersections = board.getLineIntersections();
        for (LineIntersection li : intersections) {
            Slot pivot = li.getPivot();
            LineIntersectionState lis = li.getLineInterectionState();
            SlotState originalSlotState = li.getOriginalSlotState();
            if (
                    lis == LineIntersectionState.ONE_ONE
            ) {
                if (originalSlotState != turn) {
                    addValue(pivot, Values.INTERSECTION_ONE_ONE_PIVOT);
                }
                ArrayList<Slot> slots = li.getAdjacentSlots();
                for (Slot slot : slots) {
                    addValue(slot, Values.INTERSECTION_ONE_ONE_ADJACENT);
                }
            }
            if (
                    lis == LineIntersectionState.TWO_TWO
                    && pivot.isAvailable()
            ) {
                addValue(pivot, Values.INTERSECTION_TWO_TWO);
            }
        }
    }

    /**
     * If a slot in the array provided has a value greater that all other slots, its column will be returned.
     * If there are more than 1 slots with the same maximum value, then the values of their top slots will
     * be considered. There are cases that there are no more top slots to consider. In such the answer will be
     * given at random.
     */
    private int handleTiesTop(Slot[] row) {
        int[] values = new int[row.length];
        int max = Integer.MIN_VALUE;
        int secondMax = max;
        int index = -1;
        for (int i = 0; i < row.length; i++) {
            if (row[i] == null) {
                values[i] = Integer.MIN_VALUE;
                continue;
            }

            int value = getValue(row[i]);
            values[i] = value;
            if (value == max) {
                secondMax = max;
            }
            if (value > max) {
                secondMax = max;
                max = value;
                index = i;
            }
        }
        if (secondMax != max) return index;

        Slot[] newRow = new Slot[maxCol];

        ArrayList<Integer> tyingIndices = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == max) {
                newRow[i] = row[i].getTop();
                tyingIndices.add(i);
            }
        }

        boolean chooseRandom = true;
        for (Slot slot : newRow) {
            if (slot != null) {
                chooseRandom = false;
            }
        }
        if (chooseRandom) {
            return tyingIndices.get(r.nextInt(tyingIndices.size()));
        }

        return handleTiesTop(newRow);
    }

    private int bestColumn() {
        return handleTiesTop(board.getAvailableSlots());
   }

    /**
     * Assigns values for traps that may occur in the same line
     */
    private void traps(Slot beforeZero, Slot afterThree, Line line) {
        /*this function works in the same way for both setting up a trap or defending against one. In the example of
        trap 1, if assuming that its the opponent's trap, if neither 0 or 3 is played in the very next move, the
        opponent will secure a victory by playing index 3 which will make indices 0 and "a" available to connect 4*/

        Slot[] slotsArray = line.getSlotsArray();
        ArrayList<Integer> indices = line.getIndices();
        //trap 1
        //_ - - _ _
        //0 1 2 3 a
        if (afterThree != null && afterThree.isAvailable()) {
            if (!indices.contains(1) && !indices.contains(2)) {
                if (slotsArray[0].isAvailable() && slotsArray[3].isAvailable()) {
                    addValue(slotsArray[0], Values.TRAP);
                    addValue(slotsArray[3], Values.TRAP);
                }
            }
        }

        //trap 2
        //_ _ - - _
        //b 0 1 2 3
        if (beforeZero != null && beforeZero.isAvailable()) {
            if (!indices.contains(1) && !indices.contains(2)) {
                if (slotsArray[0].isAvailable() && slotsArray[3].isAvailable()) {
                    addValue(slotsArray[0], Values.TRAP);
                    addValue(slotsArray[3], Values.TRAP);

                }
            }
        }

        //trap 3
        //_ - _ - _
        //0 1 2 3 a
        if (afterThree != null && afterThree.isAvailable()) {
            if (slotsArray[2].isAvailable()) {
                if (!indices.contains(1) && !indices.contains(3)) {
                    addValue(slotsArray[2], Values.TRAP);
                }
            }
        }

        //trap 4
        //_ - _ - _
        //b 0 1 2 3
        if (beforeZero != null && beforeZero.isAvailable()) {
            if (slotsArray[1].isAvailable()) {
                if (!indices.contains(0) && !indices.contains(2)) {
                    addValue(slotsArray[1], Values.TRAP);
                }
            }
        }
    }

    private void addValue(Slot s, int v) {
        if (shouldChangeSlotValue(s.getValue(), v)) {
            if (s.getRow() == 0) return;
            s.addValue(v);
        }
    }
    private void setValue(Slot s, int v) {
        if (shouldChangeSlotValue(s.getValue(), v)) {
            s.setValue(v);
        }
    }
    private int linesValue(Slot s){
        int row = s.getRow();
        if (row == 0) return 0;

        int count = 0;
        ArrayList<Line> lines = s.getLines();

        for (Line line : lines) {
            if (line.getLineOrientation() == LineOrientation.VERTICAL) continue;

            LineState lineState = line.getLineState();

            if (lineState == LineState.EMPTY) {
                count += Values.EMPTY_LINE;
            }
            if (line.getOriginalSlotState() == turn) {
                if (lineState == LineState.ONE) {
                    count += Values.LINE_ONE;
                }
                if (lineState == LineState.TWO) {
                    count += Values.LINE_TWO;
                }
            }
        }
        return count;
    }
    private int getValue(Slot s) {
        int value = s.getValue();
        if (
                value != Values.WIN
                || value != Values.WIN_OPPONNENT
                || value != Values.WIN_DONT_PLAY
                || value != Values.WIN_OPPONENT_DONT_PLAY
        ) {
            int row = s.getRow();

            return linesValue(s)
                    //+ row
                    + value;
        }

        return value;
    }
    private boolean shouldChangeSlotValue(int currentValue, int newValue) {
        if (newValue == Values.WIN) return true;

        if (currentValue != Values.WIN && newValue == Values.WIN_OPPONNENT) return true;

        if (currentValue == Values.WIN) return false;
        if (currentValue == Values.WIN_DONT_PLAY) return false;
        if (currentValue == Values.WIN_OPPONNENT) return false;

        return true;
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
    private void printBestOfCollumn() {
        System.out.println(Values.ANSI_WHITE);
        System.out.println("----------------------------------------------------");
        for (int c = 0; c < maxCol; c++) {
            if (!slots[0][c].isEmpty()) {
                System.out.printf("%-7s", "X");
            } else {
                for (int r = maxRow - 1; r >= 0; r--) {
                    if (slots[r][c].isEmpty()) {
                        int value = getValue(slots[r][c]);
                        System.out.printf("%-7d", value);
                        break;
                    }
                }
            }
        }
        System.out.println();
    }
    private void printLines() {
        for (int i = 0; i < lines.size(); i++) {
            System.out.println(lines.get(i).toString(i) + Values.ANSI_WHITE);
        }
        System.out.println();

    }
    private void printValues() {
        System.out.println();
        for (int r = 0; r < maxRow; r++) {
            for (int c = 0; c < maxCol; c++) {
                if (slots[r][c].getState() == SlotState.E)
                    System.out.printf("%-7d", getValue(slots[r][c]));
                if (slots[r][c].getState() == SlotState.R) System.out.printf("%-7s", "+");
                if (slots[r][c].getState() == SlotState.Y) System.out.printf("%-7s", "-");
            }
            System.out.println();
        }
    }
}
