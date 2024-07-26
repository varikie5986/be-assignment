package com.nathapot.assignment.service;

import com.nathapot.assignment.exception.CommonException;
import com.nathapot.assignment.model.UploadCsvResponseModel;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UploadCsvServiceTests {

    @InjectMocks
    private UploadCsvService uploadCsvService;

    @Mock
    private LookupService lookupService;

    @Mock
    private ExternalService externalService;

    @Mock
    private ExecutorService executorService;

    @Test
    public void checkWebsite_successWithOnlyUp() throws CommonException, IOException, ExecutionException, InterruptedException {
      MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", ("https://www.google.com," + '\n' + "https://www.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(HttpStatus.OK.toString()));

        UploadCsvResponseModel expectedResponse = new UploadCsvResponseModel();
        expectedResponse.setUp(2);
        expectedResponse.setDown(0);
        expectedResponse.setTotal(2);


        UploadCsvResponseModel response = uploadCsvService.uploadCsv(file, sseEmitter, guid);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void checkWebsite_successWithOnlyDown() throws CommonException, IOException, ExecutionException, InterruptedException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", ("http://www.google.com," + '\n' + "https://ww.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(HttpStatus.INTERNAL_SERVER_ERROR.toString()));


        UploadCsvResponseModel expectedResponse = new UploadCsvResponseModel();
        expectedResponse.setUp(0);
        expectedResponse.setDown(2);
        expectedResponse.setTotal(2);


        UploadCsvResponseModel response = uploadCsvService.uploadCsv(file, sseEmitter, guid);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void checkWebsite_successWithBothUpDown() throws CommonException, IOException, ExecutionException, InterruptedException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", ("https://www.google.com," + '\n' + "http://ww.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(HttpStatus.OK.toString()), ConcurrentUtils.constantFuture(HttpStatus.INTERNAL_SERVER_ERROR.toString()));


        UploadCsvResponseModel expectedResponse = new UploadCsvResponseModel();
        expectedResponse.setUp(1);
        expectedResponse.setDown(1);
        expectedResponse.setTotal(2);


        UploadCsvResponseModel response = uploadCsvService.uploadCsv(file, sseEmitter, guid);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void checkWebsite_failWithIncorrectTypeOfFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "text/jpg", ("https://www.google.com," + '\n' + "http://ww.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        assertThatExceptionOfType(CommonException.class).isThrownBy(() -> {
            uploadCsvService.uploadCsv(file, sseEmitter, guid);
        });
    }

    @Test
    public void checkWebsite_failWithNotHaveExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "test", "text/csv", ("https://www.google.com," + '\n' + "http://ww.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        assertThatExceptionOfType(CommonException.class).isThrownBy(() -> {
            uploadCsvService.uploadCsv(file, sseEmitter, guid);
        });
    }

    @Test
    public void checkWebsite_failWithoutHavingFileName() {
        MockMultipartFile file = new MockMultipartFile("file", "", "text/csv", ("https://www.google.com," + '\n' + "http://ww.youtube.com,").getBytes());
        String guid = "test-guid";
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        assertThatExceptionOfType(CommonException.class).isThrownBy(() -> {
            uploadCsvService.uploadCsv(file, sseEmitter, guid);
        });
    }

}
