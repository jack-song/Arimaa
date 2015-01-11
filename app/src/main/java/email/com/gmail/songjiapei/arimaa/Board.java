package email.com.gmail.songjiapei.arimaa;

import android.graphics.Point;
import email.com.gmail.songjiapei.arimaa.Piece.PieceColour;
import email.com.gmail.songjiapei.arimaa.Piece.PieceType;

//never accessed by GameView and up, organizes squares and keeps track of positions, works only by grid coordinates bottom left origin
public class Board {
		
	//squares on board
	Square[][] squares = new Square[8][8];

	//imaginary square for the piece being held by user
	Square held;
	
	public Board() {
		
		held = new Square();
		
		for(int i = 0; i < 8; i++)
			for(int k = 0; k < 8; k++)
			{
				squares[i][k] = new Square();
			}
		
		squares[0][5].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.SILVER));
		squares[1][5].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.SILVER));
		squares[2][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
		squares[3][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
		squares[4][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
		squares[5][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
		squares[6][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
		squares[7][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
		
		squares[0][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[1][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[2][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[3][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[4][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[5][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[6][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		squares[7][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
		
		squares[0][2].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.GOLD));
		squares[1][2].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.GOLD));
		squares[2][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
		squares[3][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
		squares[4][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
		squares[5][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
		squares[6][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
		squares[7][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
		
		squares[0][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[1][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[2][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[3][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[4][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[5][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[6][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
		squares[7][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
	}
	
	//READ indices start at 1 (+1 from 0)
	public static String positionToString(Point p){
		switch(p.x){
		case 0:
			return "a" + (p.y + 1);
		case 1:
			return "b" + (p.y + 1);
		case 2:
			return "c" + (p.y + 1);
		case 3:
			return "d" + (p.y + 1);
		case 4:
			return "e" + (p.y + 1);
		case 5:
			return "f" + (p.y + 1);
		case 6:
			return "g" + (p.y + 1);
		default:
			return "h" + (p.y + 1);
		}
	}
	
	//READ indices start at 1 (-1 to get 0)
	public static Point stringToPosition(String str){
		switch(str.charAt(0)){
		case 'a':
			return new Point(0, Integer.parseInt(str.substring(1)) - 1);
		case 'b':
			return new Point(1, Integer.parseInt(str.substring(1)) - 1);
		case 'c':
			return new Point(2, Integer.parseInt(str.substring(1)) - 1);
		case 'd':
			return new Point(3, Integer.parseInt(str.substring(1)) - 1);
		case 'e':
			return new Point(4, Integer.parseInt(str.substring(1)) - 1);
		case 'f':
			return new Point(5, Integer.parseInt(str.substring(1)) - 1);
		case 'g':
			return new Point(6, Integer.parseInt(str.substring(1)) - 1);
		default:
			return new Point(7, Integer.parseInt(str.substring(1)) - 1);
		}
	}
	
	//check to see if any pieces are in the wanted position
	boolean isEmpty(Point p){
		return squares[p.x][p.y].isEmpty();
	}
	
	boolean heldIsEmpty(){
		return held.isEmpty();
	}
	
	Piece getPiece(Point p){
		return squares[p.x][p.y].getPiece();
	}
	
	Piece getHeldPiece(){
		return held.getPiece();
	}
	
	//ALWAYS CHECK EMPTY
	Piece.PieceColour getColour(Point p){
		
		return squares[p.x][p.y].getColour();
	}
	
	//ALWAYS CHECK EMPTY
	Piece.PieceColour getHeldColour(){
		
		return held.getColour();
	}
	
	//retrieve the heirarchy level of the piece as an int
	//ALWAYS CHECK EMPTY
	int getLevel(Point p){
		
		return squares[p.x][p.y].getLevel();
	}	
	
	//get the letter of the piece occupying the square, space if empty
	//ALWAYS CHECK EMPTY
	char getLetter(Point p){
		
		return squares[p.x][p.y].readSquare();
	}
	
	char getHeldLetter(){
		return held.readSquare();
	}
	
	//moves piece from p1 to p2 ONE DIRECTION ONLY
	//ALWAYS CHECK EMPTIES
	void makeMove(Point p1, Point p2){
		squares[p2.x][p2.y].acceptPiece(squares[p1.x][p1.y].releasePiece());
	}
	
	//picking up and putting down a piece from the hand
	//ALWAYS CHECK EMPTIES
	void pickUp(Point p){
		held.acceptPiece(squares[p.x][p.y].releasePiece());
	}
	
	void putDown(Point p){
		squares[p.x][p.y].acceptPiece(held.releasePiece());
	}
	
	//removes a piece from p
	//ALWAYS CHECK EMPTIES
	void remove(Point p){
		
		squares[p.x][p.y].releasePiece();
	}
	
	//adds a piece to p
	//ALWAYS CHECK EMPTIES
	void placeNewPiece(Point p, Piece piece){
		squares[p.x][p.y].acceptPiece(piece);
	}
	
	//assumes that the game engine will place the rabbits after user is done moving the big pieces
	void reset(){
		
		held.releasePiece();
		
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
			{
				squares[i][j].releasePiece();
			}
		
		squares[0][5].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.SILVER));
		squares[1][5].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.SILVER));
		squares[2][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
		squares[3][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
		squares[4][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
		squares[5][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
		squares[6][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
		squares[7][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
				
		squares[0][2].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.GOLD));
		squares[1][2].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.GOLD));
		squares[2][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
		squares[3][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
		squares[4][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
		squares[5][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
		squares[6][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
		squares[7][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
	}

	//reads the position of pieces from the top left downwards
	public String getState() {
		String boardState = "";
		
		for(int y = 7; y >= 0; y--)
			for(int x = 0; x < 8; x++)
				boardState += squares[x][y].readSquare();
				
		return boardState;
	}

}
