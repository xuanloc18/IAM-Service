package dev.cxl.iam_service.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.cxl.iam_service.configuration.SecurityUtils;
import dev.cxl.iam_service.dto.request.FilesSearchRequest;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.Files;
import dev.cxl.iam_service.service.UserKCLService;
import dev.cxl.iam_service.service.storage.StorageClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class StorageController {
    private final StorageClient storageClient;
    private final UserKCLService userKCLService;

    @PostMapping("/public/files")
    public APIResponse<String> createFiles(@RequestPart("file") List<MultipartFile> files) {
        String ownerID = SecurityUtils.getAuthenticatedUserID();
        return storageClient.uploadFile(files, ownerID);
    }

    @GetMapping("/public/files/{fileID}")
    public APIResponse<?> getFilePub(@PathVariable("fileID") String fileID) {
        return storageClient.getFilePub(fileID);
    }

    @GetMapping("/public/files/view-file/{fileID}")
    public ResponseEntity<InputStreamResource> getFileView(
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio) {
        // Gọi Feign Client để lấy ảnh từ Storage
        ResponseEntity<byte[]> response = storageClient.getFileViewPublic(fileID, width, height, ratio);

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

    @PreAuthorize("hasPermission('FILE','CREATE')")
    @PostMapping("/private/files")
    public APIResponse<String> createFilesPrivate(
            @RequestPart("file") List<MultipartFile> files, @RequestParam("ownerID") String ownerID) {
        String tokenClient = "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
        return storageClient.createFiles(files, ownerID);
    }

    @PreAuthorize("hasPermission('FILE','VIEW')")
    @GetMapping("/private/files/{fileID}")
    public APIResponse<Files> getFilePrivate(@PathVariable("fileID") String fileID) {
        return storageClient.getFilePrivate(fileID);
    }

    @PreAuthorize("hasPermission('FILE','VIEW')")
    @GetMapping("/private/files/view-file/{fileID}")
    public ResponseEntity<InputStreamResource> getFileViewPrivate(
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio)
            throws IOException { // Gọi Feign Client để lấy ảnh từ Storage
        ResponseEntity<byte[]> response = storageClient.getFileViewPrivate(fileID, width, height, ratio);

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

    @PreAuthorize("hasPermission('FILE','DELETE')")
    @PostMapping("/private/files/{fileID}/deleted")
    public APIResponse<String> deleteFilePrivate(@PathVariable("fileID") String fileID) {

        return storageClient.deleteFilePrivate(fileID);
    }

    @PreAuthorize("hasPermission('FILE','VIEW')")
    @GetMapping("/private/files/{fileID}/download")
    public ResponseEntity<?> download(@PathVariable("fileID") String fileId) {
        ResponseEntity<Resource> response = storageClient.downloadFile(fileId);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getBody());
    }

    @PreAuthorize("hasPermission('FILE','VIEW')")
    @PostMapping("/private/files/getFiles")
    public APIResponse<?> getFiles(@ModelAttribute FilesSearchRequest request) throws IOException {

        return storageClient.getFiles(request);
    }
}
