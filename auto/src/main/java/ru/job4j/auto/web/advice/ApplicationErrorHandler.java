package ru.job4j.auto.web.advice;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.auto.to.ErrorInfo;
import ru.job4j.auto.util.exception.IllegalRequestDataException;
import ru.job4j.auto.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Throwables.getRootCause;
import static ru.job4j.auto.util.ExceptionHandler.getMessage;
import static ru.job4j.auto.util.ExceptionHandler.logAndGetRootCause;

@Slf4j
@ControllerAdvice
public class ApplicationErrorHandler {
    private static final Map<String, Map.Entry<String, String>> CONSTRAINS_INDEX_MAP = Map.of(
            "_unique_login_idx", Map.entry("login", "this login already exists"),
            "_unique_type_idx", Map.entry("type", "this type already exists"),
            "_unique_name_idx", Map.entry("name", "this name already exists")
    );

    @ExceptionHandler({IllegalRequestDataException.class, NotFoundException.class})
    public ResponseEntity<ErrorInfo> notFoundExceptionHandler(HttpServletRequest request, NotFoundException e) {
        return logAndGetErrorInfo(request, e, false, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflictHandler(HttpServletRequest request, DataIntegrityViolationException e) {
        String rootMessage = getRootCause(e).getMessage();
        if (rootMessage != null) {
            String lowerCaseRootMessage = rootMessage.toLowerCase();
            Optional<Map.Entry<String, Map.Entry<String, String>>> detailsOnConstrains = CONSTRAINS_INDEX_MAP.entrySet().stream()
                    .filter(entry -> lowerCaseRootMessage.contains(entry.getKey()))
                    .findAny();
            if (detailsOnConstrains.isPresent()) {
                return logAndGetErrorInfo(request, e, false, HttpStatus.UNPROCESSABLE_ENTITY, "There're validation errors", Map.ofEntries(detailsOnConstrains.get().getValue()));
            }
        }
        return logAndGetErrorInfo(request, e, true, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorInfo> bindValidationHandler(HttpServletRequest request, Exception e) {
        var result = e instanceof BindException ? ((BindException) e).getBindingResult()
                : ((MethodArgumentNotValidException) e).getBindingResult();
        var details = StreamEx.of(result.getFieldErrors())
                .toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage, (first, second) -> first + "<br>" + second);
        return logAndGetErrorInfo(request, e, false, HttpStatus.UNPROCESSABLE_ENTITY, "There're validation errors", details);
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
