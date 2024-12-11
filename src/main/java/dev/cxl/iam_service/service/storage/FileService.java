package dev.cxl.iam_service.service.storage;

import dev.cxl.iam_service.dto.response.APIResponse;
import dev.cxl.iam_service.service.UserKCLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class FileService {
    private  final UserKCLService userKCLService;
    private    final StorageClient storageClient;

    private String getTokenClient() {
        return "Bearer " + userKCLService.tokenExchangeResponse().getAccessToken();
    }
    public Object files(String id){
        return storageClient.getFilePub(getTokenClient(),id);


    }

}
