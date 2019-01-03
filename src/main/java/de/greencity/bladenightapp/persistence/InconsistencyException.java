package de.greencity.bladenightapp.persistence;

public class InconsistencyException extends Exception {
    private static final long serialVersionUID = 1426910117768984637L;

    public InconsistencyException(String s) {
        super(s);
    }
}
