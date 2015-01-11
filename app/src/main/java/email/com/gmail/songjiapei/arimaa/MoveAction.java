package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;

public abstract class MoveAction extends GameAction {

	protected Point start;
	protected Point end;
	protected Piece piece;
	protected boolean push;
	
	public MoveAction(Point start, Point end, Piece piece, boolean push) {
		super();
		this.start = start;
		this.end = end;
		this.piece = piece;
		this.push = push;
	}
	
	public Point getStart(){
		return start;
	}
	
	public Point getEnd(){
		return end;
	}
	
	public Piece getPiece(){
		return piece;
	}
	
	public boolean isPush(){
		return push;
	}
}
