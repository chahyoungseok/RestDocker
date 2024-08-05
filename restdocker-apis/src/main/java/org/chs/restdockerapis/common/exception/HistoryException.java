package org.chs.restdockerapis.common.exception;

public class HistoryException extends RestDockerException{

    public HistoryException(ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
