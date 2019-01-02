package com.kdx.core.exception;

/**
 * Author  :qujuncai
 * DATE    :18/11/29
 * Email   :qjchzq@163.com
 */
public class KdxException extends Exception {
    public KdxException() {
        super();
    }

    public KdxException(String message) {
        super(message);
    }

    public KdxException(String message, Throwable cause) {
        super(message, cause);
    }

    public KdxException(Throwable cause) {
        super(cause);
    }

    public static KdxException wrap(String message) {
        return new KdxException(message);
    }

    public static KdxException wrap(String message, Throwable cause) {
        return new KdxException(message, cause);
    }
}
