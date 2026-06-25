package gde.gde_website.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class represents global exceptions handler
 * @Author: Artemii Gorelov, Egor Grishin
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * This method is used for handle all ResponseStatus Exceptions
     * @param ex - exception to be handled
     * @return code of response as well as body with message of specific response
     * @Author: Artemii Gorelov
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}