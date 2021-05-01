package board;

import board.line.Line;
import board.line.LineIntersection;
import board.line.LineOrientation;
import board.line.LineState;
import board.slot.Slot;
import board.slot.SlotState;
import game.Coords;
import game.GameScene;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public final class Board extends Pane {
    private int maxRows;
    private int maxColumns;
    public Slot[][] slots;
    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<LineIntersection> lineIntersections = new ArrayList<>();
    private ArrayList<Coords> sphereCoords = new ArrayList<>();

    public Board() {
        super();
        maxRows = GameScene.rows;
        maxColumns = GameScene.columns;

        setUpSlots();
        setUpLines();
        setUpLineIntersections();
    }

    /**
     * Initializes, fills the 2D array that holds the slots of the board and links the slots between them.
     */
    private void setUpSlots() {
        slots = new Slot[maxRows][maxColumns];

        for (int r = 0; r < maxRows ; r++) {
            for (int c = 0; c < maxColumns; c++) {
                slots[(maxRows - 1) - r][c] = new Slot( (maxRows - 1) - r, c);
            }
        }

        for (int r = maxRows - 1; r >= 0; r--) {
            for (int c = 0; c < maxColumns; c++) {
                slots[r][c].link(slots);
            }
        }
    }

    /**
     * Fills the lines list with lines of 4 slots that can be created in any orientation.
     */
    private void setUpLines() {
        for (int r = 0; r < maxRows; r++) {
            for (int c = 0; c < maxColumns; c++) {
                //right line
                if (c + 3 < maxColumns)
                    lines.add(new Line(slots[r][c], slots[r][c + 1], slots[r][c + 2], slots[r][c + 3], LineOrientation.HORIZONTAL, lines.size()));
                //bottom line
                if (r + 3 < maxRows)
                    lines.add(new Line(slots[r][c], slots[r + 1][c], slots[r + 2][c], slots[r + 3][c], LineOrientation.VERTICAL, lines.size()));
                //bottom right line
                if (r + 3 < maxRows && c + 3 < maxColumns)
                    lines.add(new Line(slots[r][c], slots[r + 1][c + 1], slots[r + 2][c + 2], slots[r + 3][c + 3], LineOrientation.DIAGONAL_RIGHT, lines.size()));
                //bottom left line
                if (r + 3 < maxRows && c - 3 >= 0)
                    lines.add(new Line(slots[r][c], slots[r + 1][c - 1], slots[r + 2][c - 2], slots[r + 3][c - 3], LineOrientation.DIAGONAL_LEFT, lines.size()));
            }
        }
    }

    /**
     * Fills the list of intersections with intersections that can be created between two lines.
     */
    private void setUpLineIntersections() {
        int count = 0;
        for (int i = 0; i < lines.size() - 1; i++) {
            Line line1 = lines.get(i);
            LineOrientation lineOrientation1 = line1.getLineOrientation();
            for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);

                //ignoring intersections with lines of the same orientation
                if (lineOrientation1 == line2.getLineOrientation()) continue;

                Slot pivot = line1.pivot(line2);
                if (pivot != null) {
                    LineIntersection li = new LineIntersection(line1, line2, pivot, count);
                    count++;
                    line1.addIntersection(li);
                    line2.addIntersection(li);
                    lineIntersections.add(li);
                }
            }
        }
    }

    public ArrayList<LineIntersection> getLineIntersections() {
        for (int i = 0; i < lineIntersections.size(); i++) {
            if (lineIntersections.get(i).shouldIgnore()) {
                lineIntersections.remove(i);
                i--;
            }
        }
        return lineIntersections;
    }

    /**
     * Fills the board with the state provided.
     * @return whether a winner has been found
     */
    public boolean fillSlotWithLines(int row, int col, SlotState state) {
        slots[row][col].setState(state);
        sphereCoords.clear();

        boolean winner = false;
        for (Line line : lines) {
            if (line.getLineState() == LineState.FOUR) {
                winner = true;
                Slot[] slots = line.getSlotsArray();
                for (Slot slot : slots) {
                    sphereCoords.add(new Coords(maxRows - 1 - slot.getRow(), slot.getCollumn()));
                }
            }
        }

        return winner;
    }

    public boolean fillSlot(int row, int col, SlotState state) {
        slots[row][col].setState(state);

        for (Line line : lines) {
            if (line.getLineState() == LineState.FOUR) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param c column to get the empty row from
     * @return the first available empty row from bottom to top, for the column c, If the column is full
     * Integer.MAX_VALUE is returned instead.
     */
    public int getEmptyRow(int c) {
        for (int i = slots.length - 1; i >= 0; i--) {
            if (slots[i][c].getState() == SlotState.E) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    public ArrayList<Coords> getSphereCoords() {
        return sphereCoords;
    }

    /**
     * Assume this example as the current state of the slots on the board:
     * <p></p>
     * <p> - - - - - - r </p>
     * <p> - - - - - - y </p>
     * <p> - - - 4 - - r </p>
     * <p> - - - r - - y </p>
     * <p> - 2 3 y 5 6 r </p>
     * <p> 1 r y r y r y </p>
     * <p></p>
     * <p>
     * <p>- = empty slot</p>
     * <p>r = slot filled with red</p>
     * <p>y = slot filled with yellow</p>
     * <p># = slot that will be returned</p>
     * </p>
     */
    public Slot[] getAvailableSlots() {
        Slot[] row = new Slot[maxColumns];
        for (int c = 0; c < maxColumns; c++) {
            for (int r = maxRows - 1; r >= 0; r--) {
                if (slots[r][c].isAvailable()) {
                    row[c] = slots[r][c];
                    break;
                }
            }
        }

        return row;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }
}
