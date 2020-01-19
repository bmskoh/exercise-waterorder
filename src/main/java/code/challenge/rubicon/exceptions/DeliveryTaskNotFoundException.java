package code.challenge.rubicon.exceptions;

public class DeliveryTaskNotFoundException extends Exception {
    private String idName;

    public DeliveryTaskNotFoundException(String idName, String errorMsg) {
        super(errorMsg);
        this.idName = idName;
    }

    public String getIdName() {
        return this.idName;
    }
}