package com.nathapot.assignment.exception;

import com.nathapot.assignment.model.ResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CommonException.class})
    public HttpEntity<ResponseModel> handleCommonServiceException(CommonException e) {
        return new ResponseModel(e.getResponseModel().getStatus()).buildError(e.getHttpStatus());
    }
}
