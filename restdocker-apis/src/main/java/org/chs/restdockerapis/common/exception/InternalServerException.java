package org.chs.restdockerapis.common.exception;

public class InternalServerException extends RestDockerException{

    public InternalServerException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
