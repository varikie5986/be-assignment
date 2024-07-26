package com.nathapot.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusModel {
    private int code;
    private String description;
}
