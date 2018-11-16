package tech.hypermiles.hypermiles.Model;

/**
 * Created by Asia on 2017-02-20.
 */

public class APIError {

    private int code;
    private String message;
    private String exceptionMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
