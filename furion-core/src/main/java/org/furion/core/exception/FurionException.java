package org.furion.core.exception;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class FurionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;

    public FurionException() {
    }

    public FurionException(String code) {
        this(code, (String) null);
    }

    public FurionException(String code, String message) {
        this(code, message, (Throwable) null);
    }

    public FurionException(String code, Throwable cause) {
        this(code, (String) null, cause);
    }

    public FurionException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
