package exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate a requested resource (like a User or Restaurant) 
 * was not found in the database.
 * * It automatically maps to an HTTP 404 response thanks to @ResponseStatus.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // <--- This annotation ensures a 403 status code
public class UnauthorizedActionException extends RuntimeException {

	
    // Spring recommends providing a unique ID for serializable classes
    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException(String message) {
        // Calls the superclass constructor (RuntimeException) with the custom message
        super(message);
    }
}
