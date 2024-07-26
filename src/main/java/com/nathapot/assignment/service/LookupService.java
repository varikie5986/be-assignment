package com.nathapot.assignment.service;

import com.nathapot.assignment.constant.CommonConstants;
import com.nathapot.assignment.model.StatusModel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LookupService {

    public StatusModel lookup(int statusCode) {
        Map<Integer, StatusModel> map = new HashMap<>();
        map.put(CommonConstants.CODE_1000, new StatusModel(CommonConstants.CODE_1000, CommonConstants.SUCCESS_CONSTANT));
        map.put(CommonConstants.CODE_9100, new StatusModel(CommonConstants.CODE_9100, CommonConstants.API_ERROR_CONSTANT));
        map.put(CommonConstants.CODE_9999, new StatusModel(CommonConstants.CODE_9999, CommonConstants.ANY_ERROR_CONSTANT));
        map.put(CommonConstants.CODE_9300, new StatusModel(CommonConstants.CODE_9300, CommonConstants.VALIDATE_INPUT_ERROR_CONSTANT));

        StatusModel responseStatusModel = map.get(statusCode);
        if (responseStatusModel == null) {
            return new StatusModel(CommonConstants.CODE_9999, CommonConstants.ANY_ERROR_CONSTANT);
        }
        return responseStatusModel;
    }
}
