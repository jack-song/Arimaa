package email.com.gmail.songjiapei.arimaa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import static android.app.PendingIntent.getActivity;

public class BackgroundView extends ImageView {
	
	private static final String PREF_BACKSELECTION = "backset_selection";

    static final String TAG = "BackgroundView";

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

        if(null == context) context = this.getContext();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
        } catch (NumberFormatException N) {
            Log.v(TAG, "Tried to backset parse " + pref.getString(PREF_BACKSELECTION, "1"));
            backset = 1;
        }
	}

	public void setWindowWidth(int windowWidth){
		width = windowWidth;
		height = windowWidth;

        setDrawable();
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
        if(back_map != null) canvas.drawBitmap(back_map, 0, 0, null);

	}
	
	public void updateGraphicsSelection(SharedPreferences pref){
		backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
		
		setDrawable();
	}

    private void setDrawable(){
        switch(backset){
            case 1:
                setImageDrawable(getResources().getDrawable(R.drawable.traditional));
                break;

            case 2:
                setImageDrawable(getResources().getDrawable(R.drawable.stone));
                break;
            case 3:
                setImageDrawable(getResources().getDrawable(R.drawable.minimalistdark));
                break;
            default:
                setImageDrawable(getResources().getDrawable(R.drawable.minimalistlight));
                break;
        }
    }

}
