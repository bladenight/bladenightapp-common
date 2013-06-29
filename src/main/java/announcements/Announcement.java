package announcements;

public class Announcement {
	
	public enum Type {
		REMINDER, NEW_FEATURE
	}
	private Type type;
	private int id;
	private String message_d;
	private String headline_d;
	private String message_e;
	private String headline_e;
	
	public Announcement(Type type, int id, String message_d, String headline_d,
			String message_e, String headline_e){
		this.id = id;
		this.headline_d = headline_d;
		this.message_d = message_d;
		this.headline_e = headline_e;
		this.message_e = message_e;
		this.type = type;
	}
	
	public int getId(){
		return id;
	}
	public String getMessageD(){
		return message_d;
	}
	public String getHeadlineD(){
		return headline_d;
	}
	public String getMessageE(){
		return message_e;
	}
	public String getHeadlineE(){
		return headline_e;
	}
	public Type getType(){
		return type;
	}
	
}
