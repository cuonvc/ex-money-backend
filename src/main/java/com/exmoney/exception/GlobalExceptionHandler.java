package com.exmoney.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.*;

import static com.exmoney.util.Utils.getNowStr;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = new ArrayList<>();
        Locale locale = request.getLocale();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error.getDefaultMessage() != null) {
                String message = messageSource.getMessage(error.getDefaultMessage(), null, locale);
                //add params to string message
                if (error.getArguments() != null) {
                    Object[] args = new Object[error.getArguments().length - 1];
                    for (int i = 1; i < error.getArguments().length; i++) {
                        args[i - 1] = error.getArguments()[i];
                    }
                    message = MessageFormat.format(message, args);
                }
                errors.add(message);
            }
        });
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("code", 1);
        resp.put("status", HttpStatus.BAD_REQUEST.name());
        resp.put("statusCode", HttpStatus.BAD_REQUEST.value());
        resp.put("message", !errors.isEmpty() ? errors.get(0) : "Error -_-");
        resp.put("data", new Object[]{}); //khong get duoc data tung field
        resp.put("dateTime", getNowStr());

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    @ExceptionHandler
    public void serviceExceptionHandler(HttpServletRequest request, HttpServletResponse response, ServiceException serviceException)  {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("code", 1); //0 (success) - 1 (failed)
        resp.put("status", serviceException.getStatus());
        resp.put("statusCode", serviceException.getStatusCode());
        resp.put("message", serviceException.getMessage());
        resp.put("data", serviceException.getArgs());
        resp.put("dateTime", serviceException.getDateTime().toString());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(serviceException.getStatusCode());
        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleAPIException(APIException exception,
                                                                   WebRequest request) {

        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }
}
