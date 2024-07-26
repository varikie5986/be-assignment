package com.nathapot.assignment.controller;

import com.nathapot.assignment.constant.CommonConstants;
import com.nathapot.assignment.model.ResponseModel;
import com.nathapot.assignment.model.UploadCsvResponseModel;
import com.nathapot.assignment.service.LookupService;
import com.nathapot.assignment.service.SseEmitterService;
import com.nathapot.assignment.service.UploadCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/document")
@CrossOrigin
public class UploadDocumentController {

    @Autowired
    private UploadCsvService uploadCsvService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private SseEmitterService sseEmitterService;

    @GetMapping("/progress")
    public SseEmitter eventEmitter() throws IOException {
        return sseEmitterService.createEmitter();
    }

    @PostMapping("/upload/csv")
    public ResponseEntity<ResponseModel> uploadCsv(@RequestParam("file") MultipartFile file, @RequestParam("guid") String guid) throws Exception {
        UploadCsvResponseModel response = uploadCsvService.uploadCsv(file, sseEmitterService.getEmitter(guid), guid);
        sseEmitterService.removeEmitter(guid);
        return ResponseEntity.ok(new ResponseModel(lookupService.lookup(CommonConstants.CODE_1000), response));
    }
}


