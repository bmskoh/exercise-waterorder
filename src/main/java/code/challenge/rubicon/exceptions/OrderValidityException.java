package code.challenge.rubicon.exceptions;

import code.challenge.rubicon.services.WaterOrderRequestAction;

public class OrderValidityException extends Exception {

    private WaterOrderRequestAction action;

    public OrderValidityException(WaterOrderRequestAction action, String errorMsg) {
        super(errorMsg);
        this.action = action;
    }

    public WaterOrderRequestAction getAction() {
        return this.action;
    }
}