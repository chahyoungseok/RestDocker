package org.chs.restdockerapis.common.exception;

public class CustomBadRequestException extends RestDockerException {

    public CustomBadRequestException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
