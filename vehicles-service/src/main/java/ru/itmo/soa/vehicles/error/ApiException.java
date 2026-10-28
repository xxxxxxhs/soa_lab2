package ru.itmo.soa.vehicles.error;

import jakarta.ws.rs.WebApplicationException;

public class ApiException extends WebApplicationException {

    public static final int UNPROCESSABLE_ENTITY = 422;

    public ApiException(int status, String message) {
        super(message, status);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(404, message);
    }

    public static ApiException unprocessable(String message) {
        return new ApiException(UNPROCESSABLE_ENTITY, message);
    }
}
