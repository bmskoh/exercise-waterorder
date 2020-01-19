package code.challenge.rubicon.exceptions;

public class OrderNotFoundException extends Exception {
    private String idName;

    public OrderNotFoundException(String idName, String errorMsg) {
        super(errorMsg);
        this.idName = idName;
    }

    public String getIdName() {
        return this.idName;
    }
}