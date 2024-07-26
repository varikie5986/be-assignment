package com.nathapot.assignment.service;

import com.nathapot.assignment.constant.CommonConstants;
import com.nathapot.assignment.exception.CommonException;
import com.nathapot.assignment.model.ResponseModel;
import com.nathapot.assignment.model.UploadCsvResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
public class UploadCsvService {

    @Autowired
    ExecutorService executorService;

    @Autowired
    private ExternalService externalService;

    @Autowired
    private LookupService lookupService;

    public UploadCsvResponseModel uploadCsv(MultipartFile file, SseEmitter sseEmitter, String guid) throws ExecutionException, InterruptedException, CommonException, IOException {
        log.info("uploadCsv with file: {}", file.getOriginalFilename());
        if (!validateMultipartFile(file)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, new ResponseModel(lookupService.lookup(CommonConstants.CODE_9300)));
        }

        List<String> urlList = getDataFromFile(file);
        return processCsvData(urlList, sseEmitter, guid);
    }

    private List<String> getDataFromFile(MultipartFile file) throws CommonException {
        log.debug("getDataFromFile...");
        List<String> urlList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String url;
            while ((url = bufferedReader.readLine()) != null) {
                urlList.add(url);
            }
            return urlList;
        } catch (IOException e) {
            log.error("Error occurred in getDataFromFile(): ", e);
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, new ResponseModel(lookupService.lookup(CommonConstants.CODE_9100)));
        }
    }

    private UploadCsvResponseModel processCsvData(List<String> urlList, SseEmitter sseEmitter, String guid) throws ExecutionException, InterruptedException, IOException {
        log.debug("processCsvData...");
        int totalRecords = urlList.size();
        log.debug("totalRecords: {}", totalRecords);
        UploadCsvResponseModel uploadCsvResponseModel = new UploadCsvResponseModel(0, 0, totalRecords);

        List<Future<String>> pendingList = new ArrayList<>();

        for (String url : urlList) {
            String preparedUrl = prepareUrl(url);
            Future<String> pendingTask = executorService.submit(() -> externalService.getExternalData(preparedUrl));
            pendingList.add(pendingTask);
        }
        log.debug("pending list size: {}", pendingList.size());

        int counter = 1;
        for (Future<String> pendingTask : pendingList) {
            int uploadPercentage = (counter * 100 / pendingList.size());
            try {
                String completedTask = pendingTask.get();
                if(!completedTask.isBlank() && completedTask.equals(HttpStatus.OK.toString())){
                    uploadCsvResponseModel.setUp(uploadCsvResponseModel.getUp() + 1);
                } else {
                    uploadCsvResponseModel.setDown(uploadCsvResponseModel.getDown() + 1);
                }

            } catch (Exception e) {
                log.error("Error occurred in getExternalData(): ", e);
                uploadCsvResponseModel.setDown(uploadCsvResponseModel.getDown() + 1);
            }
            log.debug("uploadPercentage: " + uploadPercentage);
            sseEmitter.send(SseEmitter.event().name(guid).data(uploadPercentage));
            counter++;
        }
        return uploadCsvResponseModel;
    }

    private String prepareUrl(String url) {
        log.debug("prepareUrl for url: {}", url);
        return url.replace(",", "");
    }

    private boolean validateMultipartFile(MultipartFile file) throws CommonException {
        log.debug("validateMultipartFile with file: {}", file.getOriginalFilename());
        if (file.getOriginalFilename().isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, new ResponseModel(lookupService.lookup(CommonConstants.CODE_9300)));
        }

        String filename = file.getOriginalFilename();
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex == -1) {
            return false;
        }

        String extension = filename.substring(extensionIndex + 1).toLowerCase();

        if (!extension.equals("csv")) {
            throw new CommonException(HttpStatus.BAD_REQUEST, new ResponseModel(lookupService.lookup(CommonConstants.CODE_9300)));
        }

        return true;
    }
}
