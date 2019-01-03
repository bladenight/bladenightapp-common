package de.greencity.bladenightapp.network.messages;


public class SetMinimumLinearPosition extends AdminMessage {

    public SetMinimumLinearPosition(double value, String password) {
        setValue(value);
        authenticate(password);
    }

    public double getValue() {
        return val;
    }

    public void setValue(double val) {
        this.val = val;
    }

    private double val;
}
