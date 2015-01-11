package email.com.gmail.songjiapei.arimaa;

//never accessed by Game and up, container for pieces, tracks empty squares in the game
public class Square {
		
	Piece piece;
	
	boolean empty;

	public Square() {
		piece = null;
		empty = true;
	}
	
	//get letter representation for what is in the square currently
	char readSquare(){
		if(isEmpty()){
			return ' ';
		}
		
		else return piece.getLetter();
	}
	
	Piece getPiece(){
		return piece;
	}
	
	//retrieve the letter record of the piece's colour
	//ALWAYS CHECK ISEMPTY FIRST
	Piece.PieceColour getColour(){
		return piece.getColour();
	}
	
	//retrieve the heirarchy level of the piece as an int
	//ALWAYS CHECK ISEMPTY FIRST
	int getLevel(){
		return piece.getLevel();
	}
	
	//give up currently held piece
	//ALWAYS CHECK ISEMPTY FIRST
	Piece releasePiece(){
		
		Piece temppiece = piece;
		piece = null;
		
		empty = true;
		return temppiece;
	}
	
	//accept new piece
	//ALWAYS CHECK ISEMPTY FIRST
	void acceptPiece(Piece piece){
		
		this.piece = piece;
		empty = false;
	}
	
	boolean isEmpty(){
		return empty;
	}

}
