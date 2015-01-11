package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;


public class RemoveAction extends GameAction {

	public static final char HISTORY_FLAG = 'R';
	private static final char HISTORY_SUBFLAG = 'x';
	
	protected Point position;
	protected Piece piece;
	
	public RemoveAction(Point position, Piece piece) {
		super();
		this.position = position;
		this.piece = piece;
	}
	
	public Point getPosition(){
		return position;
	}
	
	public Piece getPiece(){
		return piece;
	}

	public String toString(){
		
		return String.valueOf(HISTORY_FLAG) + piece.getLetter() + Board.positionToString(position) + HISTORY_SUBFLAG;
	}
	
	public static RemoveAction fromString(String str){
		Piece piece = new Piece(str.charAt(1));
		Point position = Board.stringToPosition(str.substring(2, 4));
		return new RemoveAction(position, piece);
	}
}
