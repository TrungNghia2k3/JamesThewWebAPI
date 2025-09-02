package com.ntn.culinary.constant;

public enum ResponseStatus {
    SUCCESS(200, "Success"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity/Validation Error"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
