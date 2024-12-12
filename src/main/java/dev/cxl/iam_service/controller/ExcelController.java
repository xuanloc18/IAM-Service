package dev.cxl.iam_service.controller;

import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.entity.UserInformation;
import dev.cxl.iam_service.respository.UserInformationRepository;
import dev.cxl.iam_service.service.UserService;
import dev.cxl.iam_service.service.excel.ExportService;
import dev.cxl.iam_service.service.excel.FileUtils;
import dev.cxl.iam_service.service.excel.ImportService;
import dev.cxl.iam_service.service.storage.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@RestController
public class ExcelController {
    private final ImportService importService;
    private final ExportService exportService;
    private final UserInformationRepository userInformationRepository;
    private final StorageClient storageClient;
    @PostMapping("/import")
    public APIResponse<List<String>> importUsers(@RequestPart("file") MultipartFile file) {
        List<String> errors;
        try {
            errors = importService.importUsers(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return APIResponse.<List<String>>builder()
                .result(errors)
                .build();
    }
    @GetMapping("/export")
    public APIResponse<Boolean> exportUsers() throws IOException {
      return APIResponse.<Boolean>builder().result(exportService.exportToStorage()).build();
    }

}
