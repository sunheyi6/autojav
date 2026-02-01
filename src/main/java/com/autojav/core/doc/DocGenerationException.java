package com.autojav.core.doc;

public class DocGenerationException extends Exception {

    public DocGenerationException(String message) {
        super(message);
    }

    public DocGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocGenerationException(Throwable cause) {
        super(cause);
    }
}
