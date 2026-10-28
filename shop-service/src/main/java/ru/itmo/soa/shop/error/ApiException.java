package ru.itmo.soa.shop.error;

public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, message);
    }

    public static ApiException unprocessable(String message) {
        return new ApiException(422, message);
    }

    public int getStatus() {
        return status;
    }
}
