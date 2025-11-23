package kma.health.app.kma_health.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ExaminationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ExaminationNotFoundException ex)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(AppointmentNotFoundException ex)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DoctorSpecializationAgeRestrictionException.class)
    public ResponseEntity<ErrorResponse> handle(DoctorSpecializationAgeRestrictionException ex)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidCredentialsException ex){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(RoleNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FeedbackNotPermitted.class)
    public ResponseEntity<ErrorResponse> handle(FeedbackNotPermitted ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingOpenAppointmentException.class)
    public ResponseEntity<ErrorResponse> handle(MissingOpenAppointmentException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFamilyDoctorReferralMethodException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidFamilyDoctorReferralMethodException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
