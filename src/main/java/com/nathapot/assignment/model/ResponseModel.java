package com.nathapot.assignment.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
public class ResponseModel {
    private StatusModel status;
    private Object data;

    public ResponseModel(StatusModel status) {
        this.status = status;
    }

    public ResponseModel(StatusModel status, Object data) {
        this.status = status;
        this.data = data;
    }

    public HttpEntity<ResponseModel> buildError(HttpStatus httpStatus) {
        return new ResponseEntity<>(new ResponseModel(this.status), httpStatus);
    }
}
