package quiz.console.server.communicator;

public class EmptyResponseException extends Exception {
    public EmptyResponseException(String page) {
        super(String.format("response body from page %s is empty", page));
    }
}
