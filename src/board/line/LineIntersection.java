package board.line;

import board.slot.Slot;
import board.slot.SlotState;

import java.util.ArrayList;

/**
 * Line intersections will be helpful in assigning values, to guide the logic towards a better selection of column to
 * play. In the example below, a trap is set by "o". No matter what is played next "o" can win by landing a coin on
 * either slot 1 or 2.
 *
 * <p></p>
 * <p>- - - - - - -</p>
 * <p>- - - - - - -</p>
 * <p>2 - - o - - -</p>
 * <p>1 o o o - - -</p>
 * <p>a a o a - - -</p>
 * <p>a a a o - - -</p>
 * <p></p>
 */
public class LineIntersection {
    private int index;
    private Line line1;
    private Line line2;
    private LineIntersectionState lineInterectionState = LineIntersectionState.EMPTY_EMPTY;
    //the slot that joins the two lines in this intersection
    private Slot pivot;
    private boolean leadingToTrap = false;
    private ArrayList<Slot> adjacentSlots = new ArrayList<>();
    private SlotState originalSlotState;


    public LineIntersection(Line line1, Line line2, Slot pivot, int index) {
        this.line1 = line1;
        this.line2 = line2;
        this.pivot = pivot;
        this.index = index;

        if (
                line1.getLineOrientation() != LineOrientation.VERTICAL
                && line2.getLineOrientation() != LineOrientation.VERTICAL
        ) {
            Slot[] line1SlotArray = line1.getSlotsArray();
            Slot[] line2SlotArray = line2.getSlotsArray();
            for (Slot slot1 : line1SlotArray) {
                for (Slot slot2 : line2SlotArray) {
                    if (
                            (Math.abs(slot1.getRow() - slot2.getRow()) == 1 && slot1.getCollumn() == slot2.getCollumn())
                            || (Math.abs(slot1.getCollumn() - slot2.getCollumn()) == 1 && slot1.getRow() == slot2.getRow())
                    ) {

                        adjacentSlots.add(slot1);
                        adjacentSlots.add(slot2);
                        leadingToTrap = true;
                        break;
                    }
                }
            }
        }

        adjacentSlots.remove(pivot);
        removeDuplicates(adjacentSlots);
    }

    void update(Line line) {
        Line updatedLine = line == line1 ? line1 : line2;

        LineState updatedLineState = updatedLine.getLineState();
        SlotState line1OriginalSlotState = line1.getOriginalSlotState();
        SlotState line2OriginalSlotState = line2.getOriginalSlotState();

        //states where only one transition is possible
        if (lineInterectionState == LineIntersectionState.EMPTY_EMPTY) {
            lineInterectionState = LineIntersectionState.EMPTY_ONE;
            originalSlotState = updatedLine.getOriginalSlotState();

        }else if (updatedLineState == LineState.BLOCKED) {
            lineInterectionState = LineIntersectionState.BLOCKED;

        } else if (
                line1OriginalSlotState != SlotState.E && line2OriginalSlotState != SlotState.E
                && line1OriginalSlotState != line2OriginalSlotState
        ) {
            lineInterectionState = LineIntersectionState.BLOCKED;

        //states where more than 1 transitions are possible
        } else {
            switch (lineInterectionState) {
                //will transition to one_one or empty_two
                case EMPTY_ONE:
                    if (updatedLineState == LineState.TWO) {
                        lineInterectionState = LineIntersectionState.EMPTY_TWO;
                    } else {
                        lineInterectionState = LineIntersectionState.ONE_ONE;
                    }
                    break;

                //will transition to one_two or empty_three
                case EMPTY_TWO:
                    if (updatedLineState == LineState.ONE) {
                        lineInterectionState = LineIntersectionState.ONE_TWO;
                    } else {
                        lineInterectionState = LineIntersectionState.EMPTY_THREE;
                    }
                    break;

                //will transition to one_three or one_two
                case ONE_ONE:
                    if (updatedLineState == LineState.THREE) {
                        lineInterectionState = LineIntersectionState.ONE_THREE;
                    } else {
                        lineInterectionState = LineIntersectionState.ONE_TWO;
                    }
                    break;

                //will transition to one_three or two_two
                case ONE_TWO:
                    if (updatedLineState == LineState.THREE) {
                        lineInterectionState = LineIntersectionState.ONE_THREE;
                    } else {
                        lineInterectionState = LineIntersectionState.TWO_TWO;
                    }
                    break;
            }
        }
//        System.out.println("Updated intersection: " + index+", "+lineInterectionState + " -> " + line1 + ",\n" + line2);
    }

    /**
     * Line intersections with states: BLOCKED, ONE_THREE, TWO_THREE, EMPTY_THREE are ingored, because it is impossible
     * to trap the opponent or be trapped in them.
     */
    public boolean shouldIgnore() {
        switch (lineInterectionState) {
            case BLOCKED:
            case ONE_THREE:
            case TWO_THREE:
            case EMPTY_THREE:
                return true;
            case ONE_ONE:
                return !pivot.isEmpty();
        }
        return !isLeadingToTrap();
//        return false;
    }

    public Slot getPivot() {
        return pivot;
    }

    public LineIntersectionState getLineInterectionState() {
        return lineInterectionState;
    }

    public boolean isLeadingToTrap() {
        return leadingToTrap;
    }

    public SlotState getOriginalSlotState() {
        return originalSlotState;
    }

    private ArrayList<Slot> getAll() {
        ArrayList<Slot> slots = new ArrayList<>();
        Slot[] sa1 = line1.getSlotsArray();
        for (int i = 0; i < sa1.length; i++) {
            if (sa1[i].isAvailable())
                slots.add(sa1[i]);
        }
        Slot[] sa2 = line1.getSlotsArray();
        for (int i = 0; i < sa2.length; i++) {
            if (sa2[i].isAvailable())
                slots.add(sa2[i]);
        }
        return slots;
    }

    public ArrayList<Slot> getAllButAdjacentSlots() {
        ArrayList<Slot> slots = getAll();

        for (int i = 0; i < adjacentSlots.size(); i++) {
            slots.remove(adjacentSlots.get(i));
        }

        return slots;
    }

    public ArrayList<Slot> getAllButPivotAndAdjacents() {
        ArrayList<Slot> slots = getAll();
        slots.remove(pivot);
        for (int i = 0; i < adjacentSlots.size(); i++) {
            slots.remove(adjacentSlots.get(i));
        }
        return slots;
    }
    public ArrayList<Slot> getAdjacentSlots() {
        return adjacentSlots;
    }

    public int getIndex() {
        return index;
    }

    private static void removeDuplicates(ArrayList<Slot> slots) {
        for (int i = 0; i < slots.size() - 1; i++) {
            for (int j = i + 1; j < slots.size(); j++) {
                if (slots.get(i).equals(slots.get(j))) {
                    slots.remove(slots.get(j));
                    j--;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        LineIntersection li = (LineIntersection) obj;
        return li.line1 == line1 && li.line2 == line2;
    }
}
