package models.orders.get;

public class Response {
    int total;
    String message;
    boolean success;

    public int getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

}
