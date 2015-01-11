package email.com.gmail.songjiapei.arimaa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundView extends View {
	
	private static final String PREF_BACKSELECTION = "backset_selection";

	//bitmap of background
	private Bitmap back_map;
	
	//user's choice of which background image to use
	protected int backset = 1;
	
	//size of background image, currently use as a SQUARE
	int width, height;
	
	//tie background image choice to user selection in preferences
	public BackgroundView(Context context) {
		super(context);
		initialize(context);
	}

	public BackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public BackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}
	
	private void initialize(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
	}

	public void setWindowWidth(int windowWidth){
		width = windowWidth;
		height = windowWidth;
	}
	
	//set to square view, based on height
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
				
		//draw the background
		canvas.drawBitmap(back_map, 0, 0, null);

	}
	
	public void updateGraphicsSelection(SharedPreferences pref){
		
		Bitmap pre_board;
		backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
		
		switch(backset){
		case 1:
			pre_board = BitmapFactory.decodeResource(getResources(), R.drawable.traditional);
			break;

		case 2:
			pre_board = BitmapFactory.decodeResource(getResources(), R.drawable.stone);
			break;
		case 3:
			pre_board = BitmapFactory.decodeResource(getResources(), R.drawable.minimalistdark);
			break;
		default:
			pre_board = BitmapFactory.decodeResource(getResources(), R.drawable.minimalistlight);
			break;
		}
		
		//resize the bitmap to the size of the background
		back_map = Bitmap.createScaledBitmap(pre_board, width, height, true);
		
		pre_board.recycle();
	}

}
