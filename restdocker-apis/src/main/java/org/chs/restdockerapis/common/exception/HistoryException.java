package org.chs.restdockerapis.common.exception;

import lombok.Getter;

@Getter
public class HistoryException extends Exception {

    private HistoryExceptionCode exceptionCode;

    public HistoryException(HistoryExceptionCode exceptionCode) {
        super(exceptionCode.getDescription());
        this.exceptionCode = exceptionCode;
    }
}
