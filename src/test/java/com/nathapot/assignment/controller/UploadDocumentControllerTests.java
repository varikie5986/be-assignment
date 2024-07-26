package com.nathapot.assignment.controller;

import com.nathapot.assignment.constant.CommonConstants;
import com.nathapot.assignment.model.ResponseModel;
import com.nathapot.assignment.model.StatusModel;
import com.nathapot.assignment.model.UploadCsvResponseModel;
import com.nathapot.assignment.service.LookupService;
import com.nathapot.assignment.service.SseEmitterService;
import com.nathapot.assignment.service.UploadCsvService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UploadDocumentControllerTests {

    @InjectMocks
    private UploadDocumentController uploadDocumentController;

    @Mock
    private UploadCsvService uploadCsvService;

    @Mock
    private LookupService lookupService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Test
    public void uploadCsv_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "www.google.com,https://www.youtube.com".getBytes());
        String guid = "test-guid";
        UploadCsvResponseModel mockResponseData = new UploadCsvResponseModel();
        mockResponseData.setUp(10);
        mockResponseData.setDown(5);
        mockResponseData.setTotal(15);

        UploadCsvResponseModel expectedResponseData = new UploadCsvResponseModel();
        mockResponseData.setUp(10);
        mockResponseData.setDown(5);
        mockResponseData.setTotal(15);

        when(lookupService.lookup(CommonConstants.CODE_1000)).thenReturn(new StatusModel(CommonConstants.CODE_1000, CommonConstants.SUCCESS_CONSTANT));
        when(uploadCsvService.uploadCsv(any(), any(), anyString())).thenReturn(mockResponseData);
        when(sseEmitterService.getEmitter(anyString())).thenReturn(new SseEmitter(Long.MAX_VALUE));
        doNothing().when(sseEmitterService).removeEmitter(anyString());

        ResponseEntity<ResponseModel> response = uploadDocumentController.uploadCsv(file, guid);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseModel responseModel = response.getBody();
        assertThat(responseModel.getData()).isInstanceOf(UploadCsvResponseModel.class);
        UploadCsvResponseModel uploadCsvResponse = (UploadCsvResponseModel) responseModel.getData();
        assertThat(uploadCsvResponse).usingRecursiveComparison().isEqualTo(expectedResponseData);
    }

    @Test
    public void createEmitter_success() throws Exception {
        when(sseEmitterService.createEmitter()).thenReturn(new SseEmitter(Long.MAX_VALUE));
        assertThat(uploadDocumentController.eventEmitter()).isInstanceOf(SseEmitter.class);
    }
}
