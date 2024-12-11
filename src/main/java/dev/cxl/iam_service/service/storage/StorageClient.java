package dev.cxl.iam_service.service.storage;

import java.util.List;

import com.nimbusds.jose.util.Resource;
import dev.cxl.iam_service.dto.request.FilesSearchRequest;
import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.dto.response.Files;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "storage-client", url = "${storage.service.url}")
public interface StorageClient {
    @PostMapping(value = "/public/files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<String> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestPart("file") List<MultipartFile> files,
            @RequestParam("ownerID") String ownerID);

    @GetMapping("/public/files/{fileID}")
    APIResponse<dev.cxl.iam_service.dto.response.Files> getFilePub(
            @RequestHeader("Authorization") String token, @PathVariable("fileID") String fileID);
//note
    @GetMapping("/public/files/view-file/{fileID}")
    ResponseEntity<byte[]> getFileViewPublic(
            @RequestHeader("Authorization") String token,
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio);

   //PRIVATE
   @PostMapping(value = "/private/files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    APIResponse<String> createFiles(  @RequestHeader("Authorization") String token,
           @RequestPart("file") List<MultipartFile> files, @RequestParam("ownerID") String ownerID);

    @GetMapping("/private/files/{fileID}")
    APIResponse<Files> getFilePrivate(@RequestHeader("Authorization") String token, @PathVariable("fileID") String fileID) ;

    @GetMapping("/private/files/view-file/{fileID}")
    ResponseEntity<byte[]> getFileViewPrivate(
            @RequestHeader("Authorization") String token,
            @PathVariable("fileID") String fileID,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "ratio", required = false) Double ratio);

    @PostMapping("/private/files/{fileID}/deleted")
    APIResponse<String> deleteFilePrivate(  @RequestHeader("Authorization") String token,@PathVariable("fileID") String fileID);

    @GetMapping("/private/files/{fileID}/download2")
    ResponseEntity<Resource> downloadFile(@RequestHeader("authorization") String authorizationHeader,
                                          @PathVariable("fileID") String fileId);

    @GetMapping("/private/files/getFiles")
    APIResponse<?> getFiles(  @RequestHeader("Authorization") String token,@ModelAttribute FilesSearchRequest request);

}
