package email.com.gmail.songjiapei.arimaa;

//never accessed by Board or up, defines and tracks basic properties of each piece
public class Piece {
		
	public enum PieceType {
		RABBIT, CAT, DOG, HORSE, CAMEL, ELEPHANT
	}
	
	public enum PieceColour{
		GOLD, SILVER
	}
	
	private PieceType type;
	private PieceColour colour;
	
	//makes a piece based on standard enums
	public Piece(PieceType type, PieceColour colour) {
		this.type = type;
		this.colour = colour;
	}
	
	//makes a piece based on letter representation
	public Piece(char letter){
		this.type = convertTypeFromLetter(letter);
		this.colour = convertColourFromLetter(letter);
	}
	
	//get piece type information from letter representation
	static PieceType convertTypeFromLetter(char letter){
		switch(letter){
		case 'R':
		case 'r':
			return PieceType.RABBIT;
			
		case 'C':
		case 'c':
			return PieceType.CAT;
			
		case 'D':
		case 'd':
			return PieceType.DOG;
			
		case 'H':
		case 'h':
			return PieceType.HORSE;
			
		case 'M':
		case 'm':
			return PieceType.CAMEL;
	
		default:
			return PieceType.ELEPHANT;
		}
	}

	//get colour information from letter representation
	static PieceColour convertColourFromLetter(char letter){
		switch(letter){
		case 'g':
		case 'G':
		case 'w':
		case 'W':
			return PieceColour.GOLD;
	
		default:
			return PieceColour.SILVER;
		}
	}

	//retrieve the letter record of the piece's colour
	PieceColour getColour(){
		return colour;
	}
	
	public PieceType getType(){
		return type;
	}
	
	//retrieve the heirarchy level of the piece as an int
	int getLevel(){
		switch(type){
		case RABBIT:
			return 1;
			
		case CAT:
			return 2;
			
		case DOG:
			return 3;
			
		case HORSE:
			return 4;
			
		case CAMEL:
			return 5;

		default:
			return 6;
		}
	}
	
	//retrieve the name of the piece as a letter
	char getLetter(){
		switch(type){
		case RABBIT:
			if(colour == PieceColour.GOLD)
				return 'R';
			else
				return 'r';
			
		case CAT:
			if(colour == PieceColour.GOLD)
				return 'C';
			else
				return 'c';
			
		case DOG:
			if(colour == PieceColour.GOLD)
				return 'D';
			else
				return 'd';
			
		case HORSE:
			if(colour == PieceColour.GOLD)
				return 'H';
			else
				return 'h';
			
		case CAMEL:
			if(colour == PieceColour.GOLD)
				return 'M';
			else
				return 'm';

		default:
			if(colour == PieceColour.GOLD)
				return 'E';
			else
				return 'e';
		}
	}
	
	boolean isSameColour(Piece other){
		return colour == other.getColour();
	}
	
	boolean isBigger(Piece other){
		return (getLevel() > other.getLevel());
	}
}
