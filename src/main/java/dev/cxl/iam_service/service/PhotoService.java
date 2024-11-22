package dev.cxl.iam_service.service;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.respository.UserRespository;

@Service
public class PhotoService {
    @Autowired
    UserRespository userRespository;

    public void uploadPhoto(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("");
        }
        byte[] bytes = file.getBytes();
        String avatar = Base64.encodeBase64String(bytes);
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRespository.findById(id).orElseThrow(() -> new RuntimeException(""));
        user.setAvatar(avatar);
        userRespository.save(user);
    }
}
