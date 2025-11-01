package dto;


import java.util.Date;

import lombok.Getter; // Using Lombok for clean code

@Getter // Only need getters for reading the error details
public class ErrorDetails {
    
    private Date timestamp;
    private String message;
    private String details; // A detailed description of the error path/request

    /**
     * Constructor for creating a standardized error response object.
     * @param timestamp The time the error occurred.
     * @param message A high-level description of the error (e.g., "Resource Not Found").
     * @param details A specific description from the request context (e.g., the URL path).
     */
    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
