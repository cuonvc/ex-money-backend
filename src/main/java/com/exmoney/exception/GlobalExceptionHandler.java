package com.exmoney.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    @ExceptionHandler(ServiceException.class)
    public void serviceExceptionHandler(HttpServletRequest request, HttpServletResponse response, ServiceException serviceException)  {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", serviceException.getStatus());
        resp.put("statusCode", serviceException.getStatusCode());
        resp.put("message", serviceException.getMessage());
        resp.put("data", serviceException.getArgs());
        resp.put("dateTime", serviceException.getDateTime().toString());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(serviceException.getStatusCode());
        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Problem> handleTokenException(WebRequest request, Exception exception) throws IOException {
////        if (exception instanceof SignatureException) {
////            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(exception.getMessage()));
////        } else if (exception instanceof MalformedJwtException) {
////            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(exception.getMessage()));
////        } else if (exception instanceof ExpiredJwtException) {
////            return new ResponseEntity<>(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(exception.getMessage()));
////        } else if (exception instanceof Unsu)
//
//        return ResponseEntity.of(
//                Optional.of(
//                        Problem.builder()
//                                .withTitle("Bad Request")
//                                .withDetail(exception.getMessage())
//                                .withStatus(Status.BAD_REQUEST)
//                                .build()
//                )
//        );
//    }
}
