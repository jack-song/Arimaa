package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;

public class PlaceMove extends MoveAction {
	
	public static final char HISTORY_FLAG = 'P';
	
	public PlaceMove(Point start, Point end, Piece piece) {
		super(start, end, piece, false);
	}
	
	public String toString(){
		return String.valueOf(HISTORY_FLAG) + piece.getLetter() + Board.positionToString(start) + Board.positionToString(end);
	}
	
	public static PlaceMove fromString(String str){
		Piece piece = new Piece(str.charAt(1));
		Point start = Board.stringToPosition(str.substring(2, 4));
		Point end = Board.stringToPosition(str.substring(4, 6));
		return new PlaceMove(start, end, piece);
	}
}
