package dev.cxl.iam_service.exception;

import dev.cxl.iam_service.dto.request.APIResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Log4j2
@ControllerAdvice
public class GobalExceptionHander {

    private static final  String MIN_ATRIBUTE="min";


    @ExceptionHandler(value=AppException.class)
    ResponseEntity<APIResponse> handlingRuntimeException(AppException exception){
        ErrorCode errorCode=exception.getErrorCode();
        APIResponse apiResponse=new APIResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMesage(errorCode.getMessage());
        return  ResponseEntity
                .status(errorCode.getStatusCode())
        .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<APIResponse> handResponseResponseEntity(AccessDeniedException accessDeniedException){
        ErrorCode errorCode=ErrorCode.UNAUTHORIZED;
        return  ResponseEntity.status(errorCode.getStatusCode())
                .body(APIResponse.builder()
                        .code(errorCode.getCode())
                        .mesage(errorCode.getMessage())
                        .build());

    }

    private  String mapAtribute(String message, Map<String,Object> atribute){
        String minvalue=String.valueOf(atribute.get(MIN_ATRIBUTE));
        return message.replace("{"+MIN_ATRIBUTE+"}",minvalue);
    }



}
