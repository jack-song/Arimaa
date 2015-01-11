package email.com.gmail.songjiapei.arimaa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

final public class GameView extends View {

	static final String TAG = "GameView";

	private static final String PREF_HISTORY = "history";
	private static final String PREF_BOARDSTATE = "boardstate";
	private static final String PREF_PIECESELECTION = "pieceset_selection";

	// ///////////////////////////////////////////////drawing
	// members/////////////////

	// bitmaps of pieces spritesheet and rectangles for placing the pieces
	// bitmap should always already be resized to current screen
	Bitmap pieces;
	Rect src, dst;
	Point dstp, srcp;

	// selection for pieces graphics to be used
	int pieceset;

	// number of rows and columns in spritesheet
	static final int IMGROWS = 2;
	static final int IMGCOLS = 6;

	// number of tiles per side
	static final int TILES = 8;

	// pixel size of each tile
	int tilesize;

	// if two player view is being used
	boolean twoview;

	// /////////////////////////////////////////////////interaction
	// members/////////////
	static final int INVALID_POINTER_ID = -1;

	int touchx;
	int touchy;

	int activePointer = INVALID_POINTER_ID;

	// /////////////////////////////////////////////////game
	// members////////////////////

	GameEngine game;

	public GameView(Context context) {
		super(context);
		initialize(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}

	private void initialize(Context context) {
		game = new GameEngine();
		twoview = true;
		tilesize = 0;
		pieceset = 2;
		dstp = new Point();
		srcp = new Point();
	}

	public void loadGame(SharedPreferences pref) {
		// load game if possible
		game.replayHistory(pref.getString(PREF_HISTORY, ""));
		game.loadBoardState(pref.getString(PREF_BOARDSTATE, ""));
	}

	public void saveGame(SharedPreferences pref) {
		SharedPreferences.Editor save = pref.edit();
		save.putString(PREF_HISTORY, game.getHistory());
		save.putString(PREF_BOARDSTATE, game.getBoardState());
		save.commit();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// temporary boolean to record if the image being drawn should be
		// flipped
		boolean imgflip = (twoview && !game.isGoldTurn());

		// draw all the pieces on tiles
		for (int i = 0; i < TILES; i++)
			for (int k = 0; k < TILES; k++) {
				dstp.set(i, k);

				// destination rectangle is currently iterated tile
				dst = getRectFromPosition(dstp);

				// sprite positions are origin top left, RETURNS NULL IF NO
				// MATCH/EMPTY
				srcp = getSpritePosition(game.getLetter(dstp));

				if (null == srcp)
					continue;

				src = getRectFromSpritePosition(srcp);

				if (imgflip) {
					dst = getFlippedRect(dst);
					canvas.save(Canvas.MATRIX_SAVE_FLAG); // Saving the canvas
															// and later
															// restoring it so
															// only this image
															// will be rotated.
					canvas.rotate(180);
				}

				if(pieces != null)
					canvas.drawBitmap(pieces, src, dst, null);

				if (imgflip)
					canvas.restore();

			}

		// draw the held piece if it exists
		if (!game.heldIsEmpty()) {

			srcp = getSpritePosition(game.getHeldLetter());

			src = getRectFromSpritePosition(srcp);

			dst = getMovingRectFromTouchPosition();

			if (imgflip) {
				dst = getFlippedRect(dst);
				canvas.save(Canvas.MATRIX_SAVE_FLAG); // Saving the canvas and
														// later restoring it so
														// only this image will
														// be rotated.
				canvas.rotate(180);
			}

			canvas.drawBitmap(pieces, src, dst, null);

			if (imgflip)
				canvas.restore();
		}

	}

	// set to square view, based on height
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	
	public void setWindowWidth(int windowWidth){
		tilesize = windowWidth / TILES;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			final int pointerIndex = MotionEventCompat.getActionIndex(event);
			touchx = (int) MotionEventCompat.getX(event, pointerIndex);
			touchy = (int) MotionEventCompat.getY(event, pointerIndex);

			game.trySelectSquare(getPointFromTouchPosition(new Point(touchx,
					touchy)));

			break;
		}

		case MotionEvent.ACTION_MOVE: {
			// update new coordinates for the held piece
			final int pointerIndex = MotionEventCompat.getActionIndex(event);

			touchx = (int) MotionEventCompat.getX(event, pointerIndex);
			touchy = (int) MotionEventCompat.getY(event, pointerIndex);

			break;
		}

		case MotionEvent.ACTION_UP: {
			// request to make move
			// do nothing if no piece was being held
			if (game.heldIsEmpty())
				break;

			final int pointerIndex = MotionEventCompat.getActionIndex(event);

			touchx = (int) MotionEventCompat.getX(event, pointerIndex);
			touchy = (int) MotionEventCompat.getY(event, pointerIndex);
			Point released = getPointFromTouchPosition(new Point(touchx, touchy));

			game.requestMove(released);

			// TODO: refactor with model?
			((GameActivity) getContext()).updateStatus();

			break;
		}
		}

		invalidate();
		return true;
	}

	private Point getSpritePosition(char pieceLetter) {
		switch (pieceLetter) {
		case 'E':
			return new Point(0, 0);
		case 'e':
			return new Point(0, 1);
		case 'M':
			return new Point(1, 0);
		case 'm':
			return new Point(1, 1);
		case 'H':
			return new Point(2, 0);
		case 'h':
			return new Point(2, 1);
		case 'D':
			return new Point(3, 0);
		case 'd':
			return new Point(3, 1);
		case 'C':
			return new Point(4, 0);
		case 'c':
			return new Point(4, 1);
		case 'R':
			return new Point(5, 0);
		case 'r':
			return new Point(5, 1);
		default:
			return null;
		}
	}

	public void setTwoView(boolean twoview) {
		this.twoview = twoview;
		invalidate();
	}

	// get pixel values of rectangle for drawing a piece based on tile array and
	// piece position (switching from bottom left origin to top left)
	public Rect getRectFromPosition(Point p) {
		return new Rect(p.x * tilesize, (7 - p.y) * tilesize, (p.x + 1)
				* tilesize, ((7 - p.y) + 1) * tilesize);
	}

	// get rectangle for when image needs to be flipped
	public Rect getFlippedRect(Rect r) {
		return new Rect(-r.left - tilesize, -r.top - tilesize, -r.right
				+ tilesize, -r.bottom + tilesize);
	}

	// get the rectangle for drawing the held piece
	public Rect getMovingRectFromTouchPosition() {
		int cornerx = touchx - tilesize / 2;
		int cornery = touchy - tilesize / 2;
		return new Rect(cornerx, cornery, cornerx + tilesize, cornery
				+ tilesize);
	}

	public Point getPointFromTouchPosition(Point p) {
		// get rect position by division, remember to convert from "drawing"
		// coordinates to "logic" coordinates
		Point npoint = new Point(p.x / tilesize, 7 - p.y / tilesize);

		// restrain point to proper limits if surpassed
		if (npoint.x >= TILES)
			npoint.x = TILES - 1;

		if (npoint.x < 0)
			npoint.x = 0;

		if (npoint.y >= TILES)
			npoint.y = TILES - 1;

		if (npoint.y < 0)
			npoint.y = 0;

		return npoint;
	}

	// get rectangle of sprite, origin top left, ALREADY RESIZED BITMAP
	public Rect getRectFromSpritePosition(Point p) {
		return new Rect(p.x * tilesize, p.y * tilesize, (p.x + 1) * tilesize,
				(p.y + 1) * tilesize);
	}

	// has tilesize been initialized
	boolean isTiled() {
		return tilesize > 0;
	}

	void resetGame() {
		game.resetGame();
		invalidate();
	}

	void advanceState() {
		if (game.advanceGameState(true))
			invalidate();
	}

	void revertMove() {
		game.requestRevertMove();
	}

	GameEngine.GameState getState() {
		return game.getGameState();
	}

	int getTurnSteps() {
		return game.getTurnSteps();
	}

	public void updateGraphicsSelection(SharedPreferences pref) {

		pieceset = Integer.parseInt(pref.getString(PREF_PIECESELECTION, "2"));
		
		Bitmap pre_pieces;

		switch (pieceset) {
		case 1:
			pre_pieces = BitmapFactory.decodeResource(getResources(),
					R.drawable.defaultset);
			break;

		case 3:
			pre_pieces = BitmapFactory.decodeResource(getResources(),
					R.drawable.letter);
			break;

		default:
			pre_pieces = BitmapFactory.decodeResource(getResources(),
					R.drawable.newset);
			break;
		}

		pieces = Bitmap.createScaledBitmap(pre_pieces, tilesize * IMGCOLS,
				tilesize * IMGROWS, true);

		pre_pieces.recycle();
	}

}
