package email.com.gmail.songjiapei.arimaa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;
import email.com.gmail.songjiapei.arimaa.GameEngine.GameState;


public class GameActivity extends Activity {

	static final String TAG = "GameActivity";
	static final String PREF_TWOVIEW = "twoview";
	
	//have the listener here so it can access the method to update graphics
    SharedPreferences.OnSharedPreferenceChangeListener settingsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    		updateGraphics();
    	}
    };
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		final GameView gview = (GameView) findViewById(R.id.pieces);
		final BackgroundView bview = (BackgroundView) findViewById(R.id.board);
		
		//load a game's data if possible
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
	    gview.loadGame(pref);
	    
	    //run after view load, but before view display, main purpose is to set window size before it displays
	    gview.getViewTreeObserver().addOnGlobalLayoutListener( 
	    	    new OnGlobalLayoutListener(){

	    	        @Override
	    	        public void onGlobalLayout() {

	    	            int windowWidth = gview.getWidth();

	    	            gview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	    	            
	    	            gview.setWindowWidth(windowWidth);
	    	            bview.setWindowWidth(windowWidth);
	    	            
	    	            updateGraphics();
	    	        }

	    	});
	    
		updateStatus();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(settingsListener);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(settingsListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//do not allow any orientations other than portrait
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch (item.getItemId())
		{
		case R.id.new_option:
			GameView gview = (GameView) findViewById(R.id.pieces);
			gview.resetGame();
			updateStatus();
			gview.invalidate();
			break;
		
		case R.id.settings_option:
			openSettings();
			break;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
		
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    
	    GameView gview = (GameView) findViewById(R.id.pieces);
	    
	    gview.saveGame(PreferenceManager.getDefaultSharedPreferences(this));
	}

	public void updateStatus(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean twoview = pref.getBoolean(PREF_TWOVIEW, true);
		
		GameView gview = (GameView) findViewById(R.id.pieces);
		GameEngine.GameState state = gview.getState();
		int turnSteps = gview.getTurnSteps();
		
		switch(state){
		case GOLDTURN:
			setStatus(getResources().getString(R.string.status_gold_turn_prefix) + (4 - turnSteps));
			setGoldButtonsMode();
			break;
			
		case SILVERTURN:
			setStatus(getResources().getString(R.string.status_silver_turn_prefix) + (4 - turnSteps));
			setSilverButtonsMode();
			break;
			
		case GOLDPLACE:
			setStatus(getResources().getString(R.string.status_gold_setup));	
			setGoldButtonsMode();
			break;
			
		case SILVERPLACE:
			setStatus(getResources().getString(R.string.status_silver_setup));
			setSilverButtonsMode();
			break;
		case GAMEOVERGOLD:
			setStatus(getResources().getString(R.string.status_gold_wins));	
			setNoButtonsMode();
			break;
		default:
			setStatus(getResources().getString(R.string.status_silver_wins));
			setNoButtonsMode();
		}
		
		if(!twoview && state != GameState.GAMEOVERGOLD && state != GameState.GAMEOVERSILVER)
			setGoldButtonsMode();
	}
	
	public void setStatus(String status){
		TextView goldStatus = (TextView) findViewById(R.id.status);
		TextView silverStatus = (TextView) findViewById(R.id.ustatus);
		
		goldStatus.setText(status);
		silverStatus.setText(status);
	}
	
	public void setGoldButtonsMode(){
		Button goldDoneButton = (Button) findViewById(R.id.donebutton);
		Button goldBackButton = (Button) findViewById(R.id.backbutton);
		
		Button silverDoneButton = (Button) findViewById(R.id.udonebutton);
		Button silverBackButton = (Button) findViewById(R.id.ubackbutton);
				
		goldDoneButton.setEnabled(true);
		goldBackButton.setEnabled(true);
		
		silverDoneButton.setEnabled(false);
		silverBackButton.setEnabled(false);
	}
	
	public void setSilverButtonsMode(){
		Button goldDoneButton = (Button) findViewById(R.id.donebutton);
		Button goldBackButton = (Button) findViewById(R.id.backbutton);
		
		Button silverDoneButton = (Button) findViewById(R.id.udonebutton);
		Button silverBackButton = (Button) findViewById(R.id.ubackbutton);
		
		goldDoneButton.setEnabled(false);
		goldBackButton.setEnabled(false);
		
		silverDoneButton.setEnabled(true);
		silverBackButton.setEnabled(true);
		
	}
	
	public void setNoButtonsMode(){
		Button goldDoneButton = (Button) findViewById(R.id.donebutton);
		Button goldBackButton = (Button) findViewById(R.id.backbutton);
		
		Button silverDoneButton = (Button) findViewById(R.id.udonebutton);
		Button silverBackButton = (Button) findViewById(R.id.ubackbutton);
		
		Button goldStatus = (Button) findViewById(R.id.status);
		Button silverStatus = (Button) findViewById(R.id.ustatus);
		goldStatus.setBackgroundColor(Color.TRANSPARENT);
		silverStatus.setBackgroundColor(Color.TRANSPARENT);
		
		goldDoneButton.setEnabled(false);
		goldBackButton.setEnabled(false);
		
		silverDoneButton.setEnabled(false);
		silverBackButton.setEnabled(false);
	}
		
	public void openSettings(){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void updateGraphics(){
		GameView gview = (GameView) findViewById(R.id.pieces);
		BackgroundView bview = (BackgroundView) findViewById(R.id.board);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
    	boolean twoview = pref.getBoolean(PREF_TWOVIEW, true);
    	gview.setTwoView(twoview);
		
		ViewGroup top = (ViewGroup) findViewById(R.id.topsection);
		
		if(twoview){
			top.setVisibility(View.VISIBLE);
		}
		
		else{
			top.setVisibility(View.GONE);
		}
		
		if(gview.isTiled()){
			gview.updateGraphicsSelection(pref);
			bview.updateGraphicsSelection(pref);
		}
		
	}
		
	public void doneTurn(View v){
		GameView gview = (GameView) findViewById(R.id.pieces);
		gview.advanceState();
		updateStatus();
	}
	
	public void backTurn(View v){
		GameView gview = (GameView) findViewById(R.id.pieces);
		gview.revertMove();
		updateStatus();
		gview.invalidate();
	}
	


}
