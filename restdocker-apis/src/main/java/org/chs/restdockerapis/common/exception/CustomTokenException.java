package org.chs.restdockerapis.common.exception;

public class CustomTokenException extends RestDockerException {

    public CustomTokenException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
