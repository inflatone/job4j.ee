package ru.job4j.auto.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.auto.to.ErrorInfo;
import ru.job4j.auto.util.exception.IllegalRequestDataException;
import ru.job4j.auto.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static ru.job4j.auto.util.ExceptionHandler.getMessage;
import static ru.job4j.auto.util.ExceptionHandler.logAndGetRootCause;

@Slf4j
@ControllerAdvice
public class ApplicationErrorHandler {
    @ExceptionHandler({IllegalRequestDataException.class, NotFoundException.class})
    public ResponseEntity<ErrorInfo> notFoundExceptionHandler(HttpServletRequest request, NotFoundException e) {
        return logAndGetErrorInfo(request, e, false, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> defaultExceptionHandler(HttpServletRequest request, Exception e) {
        return logAndGetErrorInfo(request, e, true, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorInfo> logAndGetErrorInfo(HttpServletRequest request, Exception e, boolean logException, HttpStatus status) {
        return logAndGetErrorInfo(request, e, logException, status, null, null);
    }

    // https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private ResponseEntity<ErrorInfo> logAndGetErrorInfo(HttpServletRequest request, Exception e, boolean logException, HttpStatus status, String message, Map<String, String> details) {
        Throwable rootCause = logAndGetRootCause(log, request, e, logException, status);
        var resp = new ErrorInfo(request.getRequestURL(), message == null ? getMessage(rootCause) : message, details);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(resp);
    }
}
