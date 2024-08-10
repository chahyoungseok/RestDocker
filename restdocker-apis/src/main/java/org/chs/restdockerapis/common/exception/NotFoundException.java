package org.chs.restdockerapis.common.exception;

public class NotFoundException extends RestDockerException {

    protected NotFoundException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
