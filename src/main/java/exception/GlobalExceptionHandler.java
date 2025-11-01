package exception;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import dto.ErrorDetails;

@ControllerAdvice // This annotation makes the class capable of handling exceptions across the whole application
public class GlobalExceptionHandler {

    /**
     * Handles custom ResourceNotFoundException (for HTTP 404 - Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)); // Provides URI details
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles custom UnauthorizedActionException (for HTTP 403 - Forbidden).
     * Used for our role-based access denial (USER trying to add restaurant).
     */
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedActionException(UnauthorizedActionException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles MethodArgumentNotValidException (for HTTP 400 - Bad Request).
     * This catches errors from @Valid annotations (e.g., trying to save a User without a name).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        // Collect all validation errors into a map for detailed reporting
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // The ErrorDetails DTO only takes one message string, so we serialize the map
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(), 
                "Input Validation Failed", 
                errors.toString());
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic illegal argument exceptions (e.g., duplicate email during registration).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}