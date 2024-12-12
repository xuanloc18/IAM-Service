package dev.cxl.iam_service.service.excel;

import org.springframework.web.multipart.MultipartFile;



import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FileUtils {

    public static MultipartFile convertToMultipartFile(ByteArrayInputStream stream, String filename) throws IOException {
        byte[] content = stream.readAllBytes();  // Đọc tất cả byte từ InputStream

        // Trả về CustomMultipartFile
        return new CustomMultipartFile(content, filename);
    }
}
