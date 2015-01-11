package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;

public class ShiftMove extends MoveAction {

	public static final char HISTORY_FLAG = 'S';
	private static final char HISTORY_PUSHFLAG = 'P';
	private static final char HISTORY_NOTPUSHFLAG = 'p';
	
	public enum Direction{
		NORTH, EAST, WEST, SOUTH
	}
	
	private Direction dir;
	
	public ShiftMove(Point start, Point end, Piece piece, boolean push) {
				
		super(start, end, piece, push);
		this.dir = getDirection(start, end);
	}
	
	public String toString(){
		return String.valueOf(HISTORY_FLAG) + piece.getLetter() + Board.positionToString(start) + getLetterFromDirection(dir) + (push ? HISTORY_PUSHFLAG: HISTORY_NOTPUSHFLAG);
	}
	
	public static ShiftMove fromString(String str){
		Piece piece = new Piece(str.charAt(1));
		Point start = Board.stringToPosition(str.substring(2, 4));
		Direction dir = getDirectionFromLetter(str.charAt(4));
		boolean push = pushFromLetter(str.charAt(5));
		return new ShiftMove(start, getEndFromStartAndDir(start, dir), piece, push);
	}
	
	public static Point getEndFromStartAndDir(Point start, Direction dir){
		switch(dir){
		case EAST:
			return new Point(start.x + 1, start.y);
		case WEST:
			return new Point(start.x - 1, start.y);
		case NORTH:
			return new Point(start.x, start.y + 1);
		default:
			return new Point(start.x, start.y - 1);
		}
	}
	
	public static boolean pushFromLetter(char push){
		if(push == 'P')
			return true;
		else
			return false;
	}
	
	//ONLY WORKS IN BOTTOM LEFT COORDINATES
	public static Direction getDirection(Point start, Point end){
		if(end.x == start.x + 1)
			return Direction.EAST;
		else if(end.x == start.x - 1)
			return Direction.WEST;
		else if(end.y == start.y + 1)
			return Direction.NORTH;
		else
			return Direction.SOUTH;
	}
	
	public static char getLetterFromDirection(Direction dir){
		switch(dir){
		case EAST:
			return 'e';
		case WEST:
			return 'w';
		case NORTH:
			return 'n';
		default:
			return 's';
		}
	}
	
	public static Direction getDirectionFromLetter(char let){
		switch(let){
		case 'e':
			return Direction.EAST;
		case 'w':
			return Direction.WEST;
		case 'n':
			return Direction.NORTH;
		default:
			return Direction.SOUTH;
		}
	}
}
