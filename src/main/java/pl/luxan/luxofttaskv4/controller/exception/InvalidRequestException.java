package pl.luxan.luxofttaskv4.controller.exception;

public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(Exception e) {
        super(e);
    }

}
