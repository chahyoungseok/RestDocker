package org.chs.restdockerapis.common.exception;

public class InValidException extends RestDockerException{

    public InValidException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
