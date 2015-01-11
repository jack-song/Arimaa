package email.com.gmail.songjiapei.arimaa;


public class DoneAction extends GameAction {

	public static final char HISTORY_FLAG = 'D';
	
	public DoneAction() {
	}

	@Override
	public String toString() {
		return String.valueOf(HISTORY_FLAG);
	}
	
	public static DoneAction fromString(String str){
		return new DoneAction();
	}

}
