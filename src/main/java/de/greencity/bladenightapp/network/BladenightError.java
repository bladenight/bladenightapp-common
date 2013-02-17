package de.greencity.bladenightapp.network;

public enum BladenightError {
	INTERNAL_ERROR("http://greencity.de/bladenightapp/internalError"),
    ;

    private BladenightError(final String text) {
        this.text = text;
    }
    
    public String getText() {
    	return text;
    }

    private final String text;

    @Override
    public String toString() {
        return "BladenightError:"+text;
    }
}