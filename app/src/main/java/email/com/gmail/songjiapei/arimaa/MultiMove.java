package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;

import java.util.ArrayList;

public class MultiMove extends MoveAction {

    public static final char HISTORY_FLAG = 'M';

    ArrayList<ShiftMove> shifts = new ArrayList<ShiftMove>();

    public MultiMove(Point start, Point end, Piece piece, ArrayList<ShiftMove> shifts) {
        super(start, end, piece, false);
        this.shifts = shifts;
    }

    public MultiMove(ShiftMove firstShift) {
        super(firstShift.getStart(), firstShift.getEnd(), firstShift.getPiece(), false);
        this.shifts.add(firstShift);
    }

    public MultiMove(MultiMove source) {
        super(source.getStart(), source.getEnd(), source.getPiece(), false);
        //copy, as this list may end up changing, but do not want source to change
        this.shifts = new ArrayList<ShiftMove>(source.getShifts());
    }

    public void addShift(ShiftMove newShift){
        this.end = newShift.getEnd();
        this.shifts.add(newShift);
    }

    public ShiftMove toShiftMove(){
        if(shifts.isEmpty() || shifts.size() > 1){
            throw new UnsupportedOperationException("MultiMove cannot be converted to a single shift move");
        }

        return shifts.get(0);
    }

    public String toString(){
        return String.valueOf(HISTORY_FLAG) + piece.getLetter() + Board.positionToString(start) + Board.positionToString(end);
    }

    public int getSteps(){
        return shifts.size();
    }

    public ArrayList<ShiftMove> getShifts() {
        return this.shifts;
    }
}
