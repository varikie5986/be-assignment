package com.nathapot.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadCsvResponseModel {
    private int up;
    private int down;
    private int total;
}
