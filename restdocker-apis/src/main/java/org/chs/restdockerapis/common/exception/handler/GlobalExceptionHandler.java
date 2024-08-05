package org.chs.restdockerapis.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.chs.globalutils.dto.GlobalResponse;
import org.chs.restdockerapis.common.exception.*;
import org.chs.restdockerapis.common.exception.CustomTokenException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final String LOG_FORMAT_INFO = "\n[🔵INFO] - ({} {})\n{}\n {}: {}";
    private final String LOG_FORMAT_ERROR = "\n[🔴ERROR] - ({} {})\n {}";

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(CustomBadRequestException customBadRequestException, HttpServletRequest request) {
        logInfo(customBadRequestException, request);

        return customBadRequestException.makeResponseEntity();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(NotFoundException notFoundException, HttpServletRequest request) {
        logInfo(notFoundException, request);

        return notFoundException.makeResponseEntity();
    }

    @ExceptionHandler(HistoryException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(HistoryException historyException, HttpServletRequest request) {
        logInfo(historyException, request);

        return historyException.makeResponseEntity();
    }

    @ExceptionHandler(CustomTokenException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(CustomTokenException customTokenException, HttpServletRequest request) {
        logInfo(customTokenException, request);

        return customTokenException.makeResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request) {
        InValidException inValidException = new InValidException(ErrorCode.ARGUMENT_NOT_VALID_EXCEPTION);
        logInfo(inValidException, request, methodArgumentNotValidException.getMessage());

        return inValidException.makeResponseEntity();
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(InternalServerException internalServerException, HttpServletRequest request) {
        logError(internalServerException, request);

        return internalServerException.makeResponseEntity();
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerCommonException(Exception exception, HttpServletRequest request) {
        logError(exception, request);

        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    private void logInfo(RestDockerException e, HttpServletRequest request) {
        log.info(LOG_FORMAT_INFO, request.getRequestURI(), request.getMethod(), e.getErrorCode(), e.getClass().getName(), e.getMessage());
    }

    private void logInfo(RestDockerException e, HttpServletRequest request, String message) {
        log.info(LOG_FORMAT_INFO, request.getRequestURI(), request.getMethod(), e.getErrorCode(), e.getClass().getName(), message);
    }

    private void logError(Exception e, HttpServletRequest request) {
        log.error(LOG_FORMAT_ERROR, request.getRequestURI(), request.getMethod(), e.getMessage());
    }
}
