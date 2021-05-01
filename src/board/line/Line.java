package board.line;

import board.slot.Slot;
import board.slot.SlotState;

import java.util.ArrayList;

/**
 * Creates an object that represents a line on the board. The lines are updated every time one of their slots are filled
 * and this will make it easier to identify if a line has four coins of the same colour
 */
public class Line {
    private int index;
    private LineState lineState = LineState.EMPTY;
    private LineOrientation lineOrientation;
    private Slot[] slotsArray = new Slot[4];

    /*storing the index of every slot to avoid comparisons between slots whenever it is required to know which slot
    is first, second, etc, in this line*/
    private ArrayList<Integer> indices = new ArrayList<>(){{
        add(0);
        add(1);
        add(2);
        add(3);
    }};

    private ArrayList<Integer> removedIndices = new ArrayList<>();

    /*when the line is filled for the first time this variable will hold the value if the slot state, to be subsequently
     compared with the next*/
    private SlotState originalSlotState = SlotState.E;

    //intersections where this line intersects another line
    private ArrayList<LineIntersection> lineIntersections = new ArrayList<>();

    public Line(Slot zero, Slot one, Slot two, Slot three, LineOrientation orientation, int index) {
        this.index = index;
        slotsArray[0] = zero;
        slotsArray[1] = one;
        slotsArray[2] = two;
        slotsArray[3] = three;
        lineOrientation = orientation;
        link();
    }

    public void update(Slot s) {
        int index = indexOf(s);
        switch (lineState) {
            case EMPTY:
                originalSlotState = s.getState();
//                indices.remove((Integer) indexOf(s));
                lineState = LineState.ONE;
                break;
            case ONE:
                indices.remove((Integer)indexOf(s));
                if (originalSlotState == s.getState()) {
                    //line has two slots filled with the same state
                    lineState = LineState.TWO;
                } else {
                    lineState = LineState.BLOCKED;
                }
                break;
            case TWO:
                reset();
//                indices.remove((Integer)indexOf(s));
                if (originalSlotState == s.getState()) {
                    //line has three slots filled with the same state
                    lineState = LineState.THREE;
                } else {
                    lineState = LineState.BLOCKED;
                }
                break;
            case THREE:
//                int index = indices.get(0);
                if (slotsArray[index].getState() != originalSlotState) lineState = LineState.BLOCKED;
                else
                    //line has four slots filled with the same state
                    lineState = board.line.LineState.FOUR;
                break;
        }
        indices.remove((Integer) index);
        removedIndices.add(index);

//        updateIntersections();        // TODO: 09/07/2020 temp comment out
    }

    public void undo(){
        if (removedIndices.isEmpty()) return;
        int index = removedIndices.get(removedIndices.size() - 1);
        indices.add(index);
        removedIndices.remove((Integer) index);
//        slotsArray[index].resetState();

        switch (lineState) {
            case FOUR:
                lineState = LineState.THREE;
                break;
            case THREE:
                lineState = LineState.TWO;
                break;
            case TWO:
                lineState = LineState.ONE;
                break;
            case ONE:
                originalSlotState = SlotState.E;
                lineState = LineState.EMPTY;
                break;
            case BLOCKED:
                boolean unblocked = true;
                int count = 0;
                for (int i = 0; i < slotsArray.length; i++) {
                    if (slotsArray[i].isEmpty()) continue;

                    if (slotsArray[i].getState() != originalSlotState) {
                        unblocked = false;
                    }
                    else {
                        count++;
                    }
                }
                if (unblocked) {
                    switch (count) {
                        case 1: lineState = LineState.ONE; break;
                        case 2: lineState = LineState.TWO; break;
                        case 3: lineState = LineState.THREE; break;
                    }
                }
                break;
        }
    }


    private void updateIntersections() {
        for (int i = 0; i < lineIntersections.size(); i++) {
            LineIntersection li = lineIntersections.get(i);
            if (li.shouldIgnore()) {
                lineIntersections.remove(li);
                i--;
            } else {
                li.update(this);
            }
        }
    }

    /**
     * Adds a line intersection to the list of line intersections that this slot falls in.
     */
    public void addIntersection(LineIntersection li) {
        lineIntersections.add(li);
    }

    public LineState getLineState() {
        return lineState;
    }

    public LineOrientation getLineOrientation() {
        return lineOrientation;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public Slot[] getSlotsArray() {
        return slotsArray;
    }

    public SlotState getOriginalSlotState() {
        return originalSlotState;
    }

    private void link() {
        for (Slot slot : slotsArray) {
            slot.link(this);
        }
    }

    private void reset() {
        for (Slot slot : slotsArray) {
            slot.resetValue();
        }
    }

    private int indexOf(Slot s) {
        for (int i = 0; i < slotsArray.length; i++) {
            if (slotsArray[i] == s) {
                return i;
            }
        }
        return  -1;
    }

    public Slot pivot(Line line) {
        for (Slot slot : this.slotsArray) {
            for (int j = 0; j < line.slotsArray.length; j++) {
                if (slot.equals(line.slotsArray[j])) {
                    return slot;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Line: " + index + " " + lineOrientation + " " + lineState + " ");
        for (Slot slot : slotsArray) {
            sb.append(slot.toString());
        }
        return sb.toString();
    }

    public String toString(int i) {
        StringBuilder sb = new StringBuilder("Line " + i + ": " + lineOrientation + " " + lineState + " ");
        for (Slot slot : slotsArray) {
            sb.append(slot.toString());
        }
        return sb.toString();
    }
}
