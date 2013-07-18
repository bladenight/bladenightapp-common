package de.greencity.bladenightapp.announcements;

public class Announcement {

	public enum Type {
		REMINDER, NEW_FEATURE
	}

	public Announcement(Type type, int id, String messageGerman, String headlineGerman,
			String messageEnglish, String headlineEnglish){
		this.id = id;
		this.headlineGerman = headlineGerman;
		this.messageGerman = messageGerman;
		this.headlineEnglish = headlineEnglish;
		this.messageEnglish = messageEnglish;
		this.type = type;
	}

	public int getId(){
		return id;
	}
	public String getMessageGerman(){
		return messageGerman;
	}
	public String getHeadlineGerman(){
		return headlineGerman;
	}
	public String getMessageEnglish(){
		return messageEnglish;
	}
	public String getHeadlineEnglish(){
		return headlineEnglish;
	}
	public Type getType(){
		return type;
	}

	private Type type;
	private int id;
	private String messageGerman;
	private String headlineGerman;
	private String messageEnglish;
	private String headlineEnglish;

}
