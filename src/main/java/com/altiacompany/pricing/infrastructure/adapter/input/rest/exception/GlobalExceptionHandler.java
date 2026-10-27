package com.altiacompany.pricing.infrastructure.adapter.input.rest.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.altiacompany.pricing.domain.exception.PriceNotFoundException;
import com.altiacompany.pricing.infrastructure.adapter.input.rest.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler that intercepts exceptions thrown by REST
 * controllers and returns structured error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link PriceNotFoundException} when no active price entity
     * matches the requested product, brand, and date criteria. Returns HTTP 404
     * (Not Found).
     *
     * @param ex
     *            the thrown domain exception
     * @param request
     *            the current HTTP servlet request
     * @return a structured {@link ErrorResponse} with HTTP status 404
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFoundException(
            PriceNotFoundException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(OffsetDateTime.now(),
                Integer.valueOf(HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles {@link MissingServletRequestParameterException} when a mandatory
     * HTTP query parameter is missing from the request. Returns HTTP 400 (Bad
     * Request).
     *
     * @param ex
     *            the Spring MVC exception for missing request parameters
     * @param request
     *            the current HTTP servlet request
     * @return a structured {@link ErrorResponse} with HTTP status 400
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(OffsetDateTime.now(),
                Integer.valueOf(HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                errorResponse);
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException} when a request
     * parameter cannot be parsed or converted into its required target type
     * (e.g. invalid date or number format). Returns HTTP 400 (Bad Request).
     *
     * @param ex
     *            the Spring MVC exception for argument type mismatches
     * @param request
     *            the current HTTP servlet request
     * @return a structured {@link ErrorResponse} with HTTP status 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(OffsetDateTime.now(),
                Integer.valueOf(HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                errorResponse);
    }

    /**
     * Fallback exception handler for any unexpected or unhandled server-side
     * {@link Exception}. Returns HTTP 500 (Internal Server Error).
     *
     * @param ex
     *            the unhandled exception
     * @param request
     *            the current HTTP servlet request
     * @return a structured {@link ErrorResponse} with HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(OffsetDateTime.now(),
                Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                errorResponse);
    }
}
