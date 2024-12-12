package dev.cxl.iam_service.service.excel;

import dev.cxl.iam_service.configuration.SecurityUtils;
import dev.cxl.iam_service.entity.UserInformation;
import dev.cxl.iam_service.respository.UserInformationRepository;
import dev.cxl.iam_service.service.UserService;
import dev.cxl.iam_service.service.storage.StorageClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExportService {
    private final UserInformationRepository userInformationRepository;
    private final StorageClient storageClient   ;

    public ByteArrayInputStream exportUsers(List<UserInformation> users) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Tạo tiêu đề
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Username", "Họ Tên", "Ngày Sinh", "Tên Đường", "Xã (Phường)", "Huyện", "Tỉnh", "Số Năm Kinh Nghiệm"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            // Ghi dữ liệu
            int rowIndex = 1;
            for (UserInformation user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getDateOfBirth().toString());
                row.createCell(3).setCellValue(user.getStreetName());
                row.createCell(4).setCellValue(user.getWard());
                row.createCell(5).setCellValue(user.getDistrict());
                row.createCell(6).setCellValue(user.getProvince());
                row.createCell(7).setCellValue(user.getYearsOfExperience());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
    public Boolean exportToStorage() throws IOException {
        ByteArrayInputStream stream = exportUsers(userInformationRepository.findAll());
        List<MultipartFile> multipartFile = new ArrayList<>();
        MultipartFile multipartFile1= FileUtils.convertToMultipartFile(stream,"users.xlsx");
        multipartFile.add(multipartFile1);
        String userID= SecurityUtils.getAuthenticatedUserID();
        storageClient.createFiles(multipartFile,userID);
        return true;
    }
}
