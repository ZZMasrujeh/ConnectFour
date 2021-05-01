package board.slot;

import board.Values;
import board.line.Line;
import board.line.LineOrientation;
import board.line.LineState;

import java.util.ArrayList;

/**
 * Represents a slot on the board.
 */
public class Slot {
    private int row;
    private int col;
    private int value = 0;
    private SlotState state = SlotState.E;

    /************************
        Neighbouring slots
    ************************/
    private Slot left;
    private Slot right;
    private Slot bottomRight;
    private Slot bottomLeft;
    private Slot topLeft;
    private Slot bottom;
    private Slot topRight;
    private Slot top;

    //the lines that this slot falls in
    private ArrayList<Line> lines;

    public Slot(int r, int c) {
        lines = new ArrayList<>();
        row = r;
        col = c;
    }

    /**
     * Assigns the neighbouring slots of this slot.
     * @param slots 2D array of the slots on the board
     */
    public void link(Slot[][] slots) {
        if (col - 1 >= 0) left = slots[row][col - 1];
        if (col + 1 < slots[0].length) right = slots[row][col + 1];
        if (row + 1 < slots.length && col - 1 >= 0) bottomLeft = slots[row + 1][col - 1];
        if (row + 1 < slots.length && col + 1 < slots[0].length) bottomRight = slots[row + 1][col + 1];
        if (row - 1 >= 0 && col + 1 < slots[0].length) topRight = slots[row - 1][col + 1];
        if (row - 1 >= 0 && col - 1 >= 0) topLeft = slots[row - 1][col - 1];
        if (row + 1 < slots.length) bottom = slots[row + 1][col];
        if (row - 1 >= 0) top = slots[row - 1][col];
    }

    /**
     * Adds a line to the list of lines that this slot falls in.
     */
    public void link(Line line) {
        lines.add(line);
    }

    public SlotState getState() {
        return state;
    }
    public void setState(SlotState state) {
        this.state = state;
        for (Line line : lines) {
            line.update(this);
        }

//        for (int i = 0; i < lines.size(); i++) {
//            if (lines.get(i).getLineState() == LineState.BLOCKED) {
//                lines.remove(lines.get(i));
//                i--;
//            }
//        }
    }

    public boolean isEmpty() {
        return state == SlotState.E;
    }
    public boolean isAvailable() {
        return (bottom == null || !bottom.isEmpty()) && isEmpty();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void resetValue() {
        value = 0;
    }
    public void addValue(int value) {
        this.value += value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public void resetState() {
        this.state = SlotState.E;
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).undo();
        }
    }
    public int getValue() {
        return value;
    }

    public Slot getLeft() {
        return left;
    }
    public Slot getRight() {
        return right;
    }
    public Slot getTopLeft() {
        return topLeft;
    }
    public Slot getBottom() {
        return bottom;
    }
    public Slot getTopRight() {
        return topRight;
    }
    public Slot getBottomRight() {
        return bottomRight;
    }
    public Slot getBottomLeft() {
        return bottomLeft;
    }
    public Slot getTop() {
        return top;
    }

    public int getCollumn() {
        return col;
    }
    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Slot) {
            Slot s = (Slot) obj;
            return s.col == this.col && s.row == this.row;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (state == SlotState.E)
            sb.append(Values.ANSI_WHITE).append("(").append(row).append(",").append(col).append(")").append(Values.ANSI_WHITE);
        else if (state == SlotState.Y)
            sb.append(Values.ANSI_YELLOW).append("(").append(row).append(",").append(col).append(")").append(Values.ANSI_WHITE);
        else
            sb.append(Values.ANSI_RED).append("(").append(row).append(",").append(col).append(")").append(Values.ANSI_WHITE);

        return sb.toString();
    }
}
