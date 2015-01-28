package email.com.gmail.songjiapei.arimaa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Point;
import android.util.Log;
import email.com.gmail.songjiapei.arimaa.Piece.PieceColour;
import email.com.gmail.songjiapei.arimaa.Piece.PieceType;
import email.com.gmail.songjiapei.arimaa.ShiftMove.Direction;

//only deals in moves or points with bottom left origin; no pixel values
//RESPONSIBLE FOR CHECKING IF SQUARES ARE EMPTY
public class GameEngine {

	private static final String TAG = "GameEngine";

	// ///////////////////////////////////////////////////////////GAME LOGIC
	// MEMBERS
	private Board board;
	private ActionList actionList;
	private boolean moveable[][] = new boolean[8][8];
	//records state of the board at the beginning of a turn
	private String boardState = "";

	private Point heldPosition;
    private Square held;

	// ///////////////////////////////////////////////////////////GAME
	// PROGRESSION MEMBERS

	public enum GameState {
		GOLDPLACE, SILVERPLACE, GOLDTURN, SILVERTURN, GAMEOVERGOLD, GAMEOVERSILVER
	}

	private GameState gameState = GameState.GOLDPLACE;

	private int turnSteps = 0;

	public GameEngine() {
		board = new Board();
        held = new Square();
		actionList = new ActionList();
	}

	void resetGame() {
        if(!held.isEmpty())
            putDown(heldPosition);
		gameState = GameState.GOLDPLACE;
		board.reset();
		actionList.clear();
		turnSteps = 0;
	}

	GameState getGameState() {
		return gameState;
	}
	
	boolean isGoldTurn() {
		switch(gameState){
		case GOLDPLACE:
		case GOLDTURN:
		case GAMEOVERGOLD:
			return true;
		default:
			return false;
		}
	}
	
	String getBoardState() {
		return boardState;
	}
	
	public void loadBoardState(String boardState) {
		this.boardState = boardState;
	}

	boolean isPlayingState() {
		return (gameState == GameState.GOLDTURN || gameState == GameState.SILVERTURN);
	}

	int getTurnSteps() {
		return turnSteps;
	}

	boolean advanceGameState(boolean record) {

		//does not have to use all moves, but boardState must be different
		if (turnSteps < 1 || board.getState().equals(boardState))
			return false;
		
		switch (gameState) {
		case GOLDTURN:
			gameState = GameState.SILVERTURN;
			turnSteps = 0;
			break;

		case SILVERTURN:
			gameState = GameState.GOLDTURN;
			turnSteps = 0;
			break;

		case GOLDPLACE:
			if (!checkGoldPlacement())
				return false;
			
			gameState = GameState.SILVERPLACE;
			turnSteps = 0;
			break;

		case SILVERPLACE:
			if (!checkSilverPlacement())
				return false;
			
			gameState = GameState.GOLDTURN;
			turnSteps = 0;
			break;
		default:
			return false;
		}

        if(!held.isEmpty())
            putDown(heldPosition);
		//update positions of the board
		boardState = board.getState();

		if(record)
			actionList.addGameAction(new DoneAction());
		
		checkWin();
		
		return true;
	}

	private void checkWin() {
			switch (gameState) {
			case GOLDTURN:
				checkSilverRabbitWin();
				checkGoldRabbitWin();
				checkNoRabbitsWin(PieceColour.GOLD, GameState.GAMEOVERSILVER);
				checkNoRabbitsWin(PieceColour.SILVER, GameState.GAMEOVERGOLD);
				break;

			case SILVERTURN:
				checkGoldRabbitWin();
				checkSilverRabbitWin();
				checkNoRabbitsWin(PieceColour.SILVER, GameState.GAMEOVERGOLD);
				checkNoRabbitsWin(PieceColour.GOLD, GameState.GAMEOVERSILVER);
				break;

			default:
                break;
			}
	}
	
	private void checkGoldRabbitWin(){
		for(int x = 0; x < 8; x++){
			Point goldWinSpot = new Point(x, 7);
			checkRabbitReachWin(goldWinSpot, PieceColour.GOLD, GameState.GAMEOVERGOLD);
		}
	}
	
	private void checkSilverRabbitWin(){
		for(int x = 0; x < 8; x++){
			Point silverWinSpot = new Point(x, 0);
			checkRabbitReachWin(silverWinSpot, PieceColour.SILVER, GameState.GAMEOVERSILVER);
		}
	}
	
	private void checkRabbitReachWin(Point p, PieceColour rabbitColour, GameState winState){
		if(!isEmpty(p))
			if(board.getPiece(p).getType() == PieceType.RABBIT && board.getPiece(p).getColour() == rabbitColour)
				gameState = winState;
	}
	
	private void checkNoRabbitsWin(PieceColour side, GameState winState){
		if(!hasRabbits(side))
			gameState = winState;
	}
	
	private boolean hasRabbits(PieceColour side){
		for(int x = 0; x < 8; x++)
			for(int y = 0; y < 8; y++){
				Point p = new Point(x, y);
				if(!isEmpty(p))
					if(board.getPiece(p).getType() == PieceType.RABBIT && board.getPiece(p).getColour() == side)
						return true;
			}
		
		return false;
	}

	void checkTraps() {
		Point[] traps = { new Point(2, 2), new Point(2, 5), new Point(5, 2),
				new Point(5, 5) };

		for (Point trap : traps) {
			if (!isEmpty(trap)) {
				if (!isSafe(trap)) {
					actionList.addGameAction(new RemoveAction(trap, board
							.getPiece(trap)));
					board.remove(trap);
				}
			}
		}
	}

	void advanceStep() {
		if (isPlayingState())
			checkTraps();

		if (turnSteps < 4)
			turnSteps++;
	}

	// finalize selection of pieces for gold, move rabbits if necessary
	// IT MUST BE GOLDPLACE gameState
	boolean checkGoldPlacement() {
		for (int x = 0; x < 8; x++) {
			for (int y = 2; y < 4; y++) {
				// squares must be empty
				if (!isEmpty(new Point(x, y))) {
					return false;
				}
			}
		}

		// ACCEPTED - drop bunnies in the required positions
		for (int xf = 0; xf < 8; xf++) {
			for (int yf = 0; yf < 2; yf++) {
				Point p = new Point(xf, yf);
				
				if (isEmpty(p))
					board.placeNewPiece(p, new Piece(PieceType.RABBIT, PieceColour.GOLD));
			}
		}

		return true;
	}

	// finalize selection of pieces for silver, move rabbits if necessary
	boolean checkSilverPlacement() {
		for (int x = 0; x < 8; x++) {
			for (int y = 4; y < 6; y++) {
				// squares must be empty
				if (!isEmpty(new Point(x, y))) {
					if (board.getLevel(new Point(x, y)) != 1)
						return false;
				}
			}
		}

		// ACCEPTED - drop bunnies in the required positions
		for (int xf = 0; xf < 8; xf++) {
			for (int yf = 6; yf < 8; yf++) {
				Point p = new Point(xf, yf);
				
				if (isEmpty(p))
					board.placeNewPiece(p, new Piece(PieceType.RABBIT, PieceColour.SILVER));
			}
		}

		return true;
	}

	// USED FOR PASSING ON INFO TO GAMEVIEW FOR DRAWING - NOT USED WITHIN ENGINE
	char getLetter(Point p) {
		return board.getLetter(p);
	}

	// USED FOR PASSING ON INFO TO GAMEVIEW FOR DRAWING - NOT USED WITHIN ENGINE
	char getHeldLetter() {
		return held.getPiece().getLetter();
	}

	// USED FOR PASSING ON INFO TO GAMEVIEW FOR DRAWING - NOT USED WITHIN ENGINE
	boolean isHeldSilver() {
		return Piece.PieceColour.SILVER == held.getColour();
	}

	// USED FOR PASSING ON INFO TO GAMEVIEW FOR DRAWING - NOT USED WITHIN ENGINE
	boolean heldIsEmpty() {
		return held.isEmpty();
	}

    void pickUp(Point p){
        held.acceptPiece(board.getPiece(p));
    }

    void putDown(Point p){
        board.remove(heldPosition);
        board.placeNewPiece(p, held.getPiece());
        held.releasePiece();
        clearMoveable();
    }

    boolean isEmpty(Point p){
            return board.isEmpty(p);
    }

	// method to shift a piece to "held" on the board, returns true if
	// successful, false if not
	// checks to see if it's reasonable to select the particular piece
	boolean trySelectSquare(Point p) {
		if (isEmpty(p) || gameState == GameState.GAMEOVERGOLD || gameState == GameState.GAMEOVERSILVER)
			return false;

		if (isPlayingState() && turnSteps >= 4)
			return false;

		// if the last move was a push, current move must finish the push
		if (!actionList.isEmpty() && actionList.wasPushing()) {
			if (couldPushLastMove(p)) {
				selectSquare(p, false);
				return true;
			} else
				return false;
		}

		// if the piece is selected on the right turn, let it be picked up, if
		// not frozen
		if (isRightTurnForSelection(board.getColour(p))){
			
			if(!isFrozen(p)) {
				selectSquare(p, false);
				return true;
			}
		}
		
		else{
			// push (currently controlled)
			if (isPlayingState() && isControlled(p) && turnSteps <= 2) {
				selectSquare(p, true);
				return true;
			}
	
			// pull (was controlled)
			if (!actionList.isEmpty() && couldGetPulledByLastMove(p)) {
				selectSquare(p, false);
				return true;
			}
		}

		return false;
	}

	void selectSquare(Point p, boolean couldBePushing) {

        //another piece is being held - return it first
        if(!heldIsEmpty()){
            returnHeld();
        }

		pickUp(p);
		heldPosition = p;

		// decide on the moves available to the piece
		setMoveable(couldBePushing);
	}

    Point getHeldPoint() {
        return heldPosition;
    }

    boolean[][] getMoveable(){
        return moveable;
    }

	// SHOULD ONLY BE CALLED BY A HUMAN
	boolean requestMove(Point p) {

        Log.v(TAG, "Move Request!");
        if (heldIsEmpty()) {
            return false;
        }

		Piece heldPiece = held.getPiece();

		// check to see if the placement of the piece is legal first
		if (!moveable[p.x][p.y] || p == heldPosition) {
			returnHeld();
			clearMoveable();
			return false;
		}

		// if the destination has a piece(SHOULD BE SAME TYPE), have it swap
		// places
		if (!isPlayingState()) {
			
			if(!isEmpty(p))
				board.makeMove(p, heldPosition);
			
			actionList.addMove(new PlaceMove(heldPosition, p, heldPiece));
		}
        Log.v(TAG, "Approved!");

		// ADD MOVE
		if (isPlayingState()) {

			if (isRightTurnForSelection(heldPiece.getColour())) {
				
				//make sure rabbits don't go backwards
				if(heldPiece.getType() == Piece.PieceType.RABBIT){
					if(heldPiece.getColour() == PieceColour.GOLD && ShiftMove.getDirection(heldPosition, p) == Direction.SOUTH){
						returnHeld();
						clearMoveable();
						return false;
					}
					if(heldPiece.getColour()== PieceColour.SILVER && ShiftMove.getDirection(heldPosition, p) == Direction.NORTH){
						returnHeld();
						clearMoveable();
						return false;
					}
				}
				
				actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), false));
			}

			// REMEMBER IF IT WAS A PUSH - PRIORITIZE PULL (LESS EXPENSIVE MOVE)
			else if (couldGetPulledByLastMove(heldPosition, held.getPiece())) {
				actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), false));
			}

			else {
				actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), true));
			}
			
		}

		putDown(p);

		clearMoveable();
		advanceStep();
		return true;
	}

	// SHOULD ONLY BE CALLED BY A cpu
	// one piece ONLY
	// SOURCE SHOULD HAVE A PIECE, DESTINATION SHOULD ALWAYS BE EMPTY
	boolean requestMove(CpuPlaceMove m) {

		if (!isEmpty(m.getEnd()) || isEmpty(m.getStart())) {
            Log.v(TAG, "CPU request rejected!");
            return false;
		}

		board.makeMove(m.getStart(), m.getEnd());
		return true;
	}

	boolean requestRevertMove() {
		if (turnSteps <= 0 || !actionList.isLastMoveShift())
			return false;

        returnHeld();
        clearMoveable();

		CpuPlaceMove revertedMove = actionList.getRevertedMove();
        Log.v(TAG, "Reverted: " + revertedMove.getPiece().getLetter());

        RemoveAction removeAction = actionList.revertMoveAndGetRemoveAction();

        // a piece was removed
        if (null != removeAction) {
            board.placeNewPiece(removeAction.getPosition(),
                    removeAction.getPiece());
        }

        requestMove(revertedMove);
        turnSteps--;
        return true;
	}
	
	public String getHistory(){
		return actionList.getHistory();
	}
	
	//technically history does not need nearly as much information as it does, but info may be useful for future features (official notation exports)
	public void replayHistory(String history){
		
		resetGame();
		
		if(history == null || history.length() == 0)
			return;
		
		ArrayList<GameAction> actions = actionList.getHistoryFromString(history);
		
		for(GameAction action: actions){
			replayAction(action);
		}
	}
	
	private void replayAction(GameAction action){
		if(action instanceof DoneAction){
			advanceGameState(true);
		}
		
		else if(action instanceof MoveAction){
			replayMove((MoveAction) action);
		}
		
		//ignore RemoveActions, as they will be added automatically
	}
	
	private void replayMove(MoveAction move){
		trySelectSquare(move.start);
		requestMove(move.end);
	}

	private void returnHeld() {
		// if there's a piece being held, put it down back where it was picked
		// up from
		if (!held.isEmpty())
			putDown(heldPosition);

	}

	boolean isSilver(Point p) {
		if (isEmpty(p)) {
			return false;
		}

		return board.getColour(p) == Piece.PieceColour.SILVER;
	}

	private boolean couldPushLastMove(Point pPusher) {
		Point followUpMove = actionList.getLastMoveSource();
		Piece pushee = actionList.getLastPiece();
		Piece pusher = board.getPiece(pPusher);

		if (null == followUpMove || null == pushee)
			return false;

		if (areAdjacent(followUpMove, pPusher)) {
			if (!pushee.isSameColour(pusher) && pusher.isBigger(pushee)) {
				if (!isFrozen(pPusher))
					return true;
			}
		}

		return false;
	}

	private boolean couldGetPulledByLastMove(Point pPullee) {
		Point destination = actionList.getLastMoveSource();
		Piece puller = actionList.getLastPiece();
		Piece pullee = board.getPiece(pPullee);

		if (null == destination || null == puller)
			return false;

		if (areAdjacent(destination, pPullee)) {
			if (!pullee.isSameColour(puller) && puller.isBigger(pullee)) {
				return true;
			}
		}

		return false;
	}

	private boolean couldGetPulledByLastMove(Point pPullee, Piece pullee) {
		Point destination = actionList.getLastMoveSource();
		Piece puller = actionList.getLastPiece();

		if (null == destination || null == puller)
			return false;

		if (areAdjacent(destination, pPullee)) {
			if (!pullee.isSameColour(puller) && puller.isBigger(pullee)) {
				return true;
			}
		}

		return false;
	}

	private boolean areAdjacent(Point p1, Point p2) {
		if (p1.y == p2.y && (p1.x == p2.x + 1 || p1.x == p2.x - 1))
			return true;
		else
            return (p1.x == p2.x && (p1.y == p2.y + 1 || p1.y == p2.y - 1));
	}

	private Set<Point> getFilledAdjacents(Point p) {

		Set<Point> filledAdjacents = new HashSet<Point>();

		Point testRight = new Point(p.x + 1, p.y);
		if (p.x < 7 && !isEmpty(testRight))
			filledAdjacents.add(testRight);

		Point testLeft = new Point(p.x - 1, p.y);
		if (p.x > 0 && !isEmpty(testLeft))
			filledAdjacents.add(testLeft);

		Point testUp = new Point(p.x, p.y + 1);
		if (p.y < 7 && !isEmpty(testUp))
			filledAdjacents.add(testUp);

		Point testDown = new Point(p.x, p.y - 1);
		if (p.y > 0 && !isEmpty(testDown))
			filledAdjacents.add(testDown);

		return filledAdjacents;
	}

	private Set<Point> getEmptyAdjacents(Point p) {

		Set<Point> emptyAdjacents = new HashSet<Point>();

		Point testRight = new Point(p.x + 1, p.y);
		if (p.x < 7 && isEmpty(testRight))
			emptyAdjacents.add(testRight);

		Point testLeft = new Point(p.x - 1, p.y);
		if (p.x > 0 && isEmpty(testLeft))
			emptyAdjacents.add(testLeft);

		Point testUp = new Point(p.x, p.y + 1);
		if (p.y < 7 && isEmpty(testUp))
			emptyAdjacents.add(testUp);

		Point testDown = new Point(p.x, p.y - 1);
		if (p.y > 0 && isEmpty(testDown))
			emptyAdjacents.add(testDown);

		return emptyAdjacents;
	}

	private boolean isRightTurnForSelection(PieceColour colour) {
		return (gameState == GameState.GOLDTURN || gameState == GameState.GOLDPLACE) == (colour == PieceColour.GOLD);
	}

	private boolean isSameColour(Point p1, Point p2) {
		return board.getPiece(p1).isSameColour(board.getPiece(p2));
	}

	// determines if first piece is larger than second
	private boolean isBigger(Point p1, Point p2) {
		return board.getPiece(p1).isBigger(board.getPiece(p2));
	}

	// checks if a bigger enemy piece is there ALWAYS p1 acting on p2 ASSUMES
	// EXISTANCE
	private boolean threatening(Point p1, Point p2) {
        return (!isSameColour(p1, p2) && isBigger(p1, p2));
	}

	// checks if a friendly piece is keeping another piece mobile ASSUMES
	// EXISTANCE
	private boolean saving(Point p1, Point p2) {
        return (isSameColour(p1, p2));
	}

	// checks if a bigger enemy piece is keeping a piece from moving
	private boolean freezing(Point p1, Point p2) {
        return (threatening(p1, p2) && !isSafe(p2));
	}

	// controlled VS frozen : adjacent friend cannot save you (don't matter),
	// but the attacker cannot be frozen
	private boolean controlling(Point p1, Point p2) {

        return (threatening(p1, p2) && !isFrozen(p1));
	}

	// checks if a square is "safe" with allies or not
	private boolean isSafe(Point p) {

		Set<Point> filledAdjacents = getFilledAdjacents(p);

		for (Point adjacent : filledAdjacents) {
			if (saving(adjacent, p)) {
				return true;
			}
		}

		return false;
	}

	// checks if a square is "frozen" by an enemy or not
	private boolean isFrozen(Point p) {

		Set<Point> filledAdjacents = getFilledAdjacents(p);

		for (Point adjacent : filledAdjacents) {
			if (freezing(adjacent, p)) {
				return true;
			}
		}

		return false;
	}

	// checks if a square is "controlled" by an enemy or not
	private boolean isControlled(Point p) {

		Set<Point> filledAdjacents = getFilledAdjacents(p);

		for (Point adjacent : filledAdjacents) {
			if (controlling(adjacent, p)) {
				return true;
			}
		}

		return false;
	}

	// ONLY CALLED ON GOLDPLACE OR SILVERPLACE
	private void setPlaceMoveable() {
		if (gameState == GameState.GOLDPLACE) {

			for (int i = 0; i < 8; i++)
				for (int k = 0; k < 2; k++)
					moveable[i][k] = true;
		}

		else {

			for (int i = 0; i < 8; i++)
				for (int k = 6; k < 8; k++)
					moveable[i][k] = true;
		}
	}

	// ASSUMES PIECE IS VIABLE TO MOVE (NOT FROZEN)
	private void setMoveable(boolean couldBePushing) {

		clearMoveable();

		// SHOULD ONLY BE BACK UP (should not have been selected in the first
		// place)
		// check if something is actually selected and there are enough turns
		if (held.isEmpty() || (isPlayingState() && turnSteps >= 4)) {
			return;
		}

		// setting moveables if game is only on placing pieces
		if (gameState == GameState.GOLDPLACE || gameState == GameState.SILVERPLACE) {
			setPlaceMoveable();
			return;
		}

		if (!actionList.isEmpty()) {
			// if the last move was a push
			if (actionList.wasPushing()) {
				moveable[actionList.getLastMoveSource().x][actionList
						.getLastMoveSource().y] = true;
				return;
			}

			// if wrong team and cannot push, only pull is available
			if (!isRightTurnForSelection(held.getColour())
					&& !couldBePushing) {
				moveable[actionList.getLastMoveSource().x][actionList
						.getLastMoveSource().y] = true;
				return;
			}
		}

		// if regular move/pushing
		Set<Point> emptyAdjacents = getEmptyAdjacents(heldPosition);

		for (Point p : emptyAdjacents) {
			moveable[p.x][p.y] = true;
		}

        //extra for rabbits on their turn
        if(getHeldLetter() == 'R' && gameState == GameState.GOLDTURN)
            moveable[heldPosition.x][heldPosition.y-1] = false;

        if(getHeldLetter() == 'r' && gameState == GameState.SILVERTURN)
            moveable[heldPosition.x][heldPosition.y+1] = false;

	}

	private void clearMoveable() {

		for (int i = 0; i < 8; i++)
			for (int k = 0; k < 8; k++)
				moveable[i][k] = false;

	}

}
