package com.nathapot.assignment.exception;

import com.nathapot.assignment.model.ResponseModel;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonException extends Exception  {
    private final HttpStatus httpStatus;
    private final transient ResponseModel responseModel;

    public CommonException(HttpStatus httpStatus, ResponseModel responseModel) {
        this.httpStatus = httpStatus;
        this.responseModel = responseModel;
    }
}
