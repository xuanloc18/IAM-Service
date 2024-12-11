package dev.cxl.iam_service.controller;

import com.nimbusds.jose.util.Resource;
import dev.cxl.iam_service.dto.request.FilesSearchRequest;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.Files;
import dev.cxl.iam_service.service.UserKCLService;
import dev.cxl.iam_service.service.storage.FileService;
import dev.cxl.iam_service.service.storage.StorageClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StorageController {
    private final StorageClient storageClient;
    private  final UserKCLService userKCLService;
    private final FileService fileService;

    private String getTokenClient() {
        return "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
    }

    @PostMapping("/public/files")
    public APIResponse<String> createFiles(
            @RequestPart("file") List<MultipartFile> files, @RequestParam("ownerID") String ownerID) {
        String tokenClient = "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
        return storageClient.uploadFile(tokenClient,files, ownerID);
    }
    @GetMapping("/public/files/{fileID}")
    public APIResponse<?> getFilePub(@PathVariable("fileID") String fileID) {
        return storageClient.getFilePub(getTokenClient(), fileID);
    }

    @GetMapping("/public/files/view-file/{fileID}")
    public ResponseEntity<InputStreamResource> getFileView(
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio)
            {
                // Gọi Feign Client để lấy ảnh từ Storage
                ResponseEntity<byte[]> response = storageClient.getFileViewPublic(getTokenClient(), fileID, width, height, ratio);

                // Lấy body từ response (byte[])
                byte[] imageBytes = response.getBody();

                // Chuyển byte[] thành InputStreamResource
                InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));
                // Tạo lại headers và trả về phản hồi
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "image/png"); // Đảm bảo Content-Type là đúng
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileID + ".png");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(imageBytes.length)
                        .body(resource);
    }
    @PostMapping("/private/files")
    public APIResponse<String> createFilesPrivate(
            @RequestPart("file") List<MultipartFile> files, @RequestParam("ownerID") String ownerID)
         {
             String tokenClient = "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
        return storageClient.createFiles(tokenClient,files, ownerID);
    }
    @GetMapping("/private/files/{fileID}")
    public APIResponse<Files> getFilePrivate(@PathVariable("fileID") String fileID) {

        return storageClient.getFilePrivate(getTokenClient(),fileID);
    }

    @GetMapping("/private/files/view-file/{fileID}")
    public ResponseEntity<InputStreamResource> getFileViewPrivate(
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio)
            throws IOException {// Gọi Feign Client để lấy ảnh từ Storage
        ResponseEntity<byte[]> response = storageClient.getFileViewPrivate(getTokenClient(), fileID, width, height, ratio);

        // Lấy body từ response (byte[])
        byte[] imageBytes = response.getBody();

        // Chuyển byte[] thành InputStreamResource
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));
        // Tạo lại headers và trả về phản hồi
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/png"); // Đảm bảo Content-Type là đúng
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileID + ".png");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(imageBytes.length)
                .body(resource);
    }

    @PostMapping("/private/files/{fileID}/deleted")
    public APIResponse<String> deleteFilePrivate(@PathVariable("fileID") String fileID) {

        return storageClient.deleteFilePrivate(getTokenClient(),fileID);
    }

    @GetMapping("/private/files/{fileID}/download")
    public ResponseEntity<?> download(@PathVariable("fileID") String fileId) {
        ResponseEntity<Resource> response = storageClient.downloadFile(getTokenClient(), fileId);
        return ResponseEntity.ok()
                .headers(response.getHeaders())
                .body(response.getBody());}



    @GetMapping("/private/files/getFiles")
    public APIResponse<?> getFiles(@ModelAttribute FilesSearchRequest request) throws IOException {

        return storageClient.getFiles(getTokenClient(),request);
    }
}
