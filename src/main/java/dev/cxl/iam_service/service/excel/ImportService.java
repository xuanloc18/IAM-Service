package dev.cxl.iam_service.service.excel;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.cxl.iam_service.entity.UserInformation;
import dev.cxl.iam_service.respository.UserInformationRepository;
import dev.cxl.iam_service.service.UtilUserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportService {
    private final UtilUserService utilUserService;
    private final UserInformationRepository userInformationRepository;

    public List<String> importUsers(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        List<UserInformation> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Đọc dữ liệu từ hàng 2 (bỏ tiêu đề)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                if (utilUserService.userExists(row.getCell(0).getStringCellValue())) {
                    errors.add("Dòng " + (i + 1) + ": Username tồn tại.");
                    continue;
                }
                if (StringUtils.isEmpty(row.getCell(0).getStringCellValue())) {
                    errors.add("Dòng " + (i + 1) + ": Username bị trống.");
                    continue;
                }
                try {

                    UserInformation user = new UserInformation();
                    user.setUsername(row.getCell(0).getStringCellValue());
                    user.setFullName(row.getCell(1).getStringCellValue());
                    user.setDateOfBirth(row.getCell(2).getLocalDateTimeCellValue().toLocalDate());
                    user.setStreetName(row.getCell(3).getStringCellValue());
                    user.setWard(row.getCell(4).getStringCellValue());
                    user.setDistrict(row.getCell(5).getStringCellValue());
                    user.setProvince(row.getCell(6).getStringCellValue());
                    user.setYearsOfExperience((int) row.getCell(7).getNumericCellValue());
                    user.setDeleted(false);
                    // Validation
                    validateUser(user, errors, i + 1);
                    users.add(user);
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + " bị lỗi: " + e.getMessage());
                }
            }
        }

        userInformationRepository.saveAll(users);

        return errors;
    }

    private void validateUser(UserInformation user, List<String> errors, int rowIndex) {
        // Kiểm tra username
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Username bị trống.");
        }

        // Kiểm tra fullName
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Họ tên bị trống.");
        }

        // Kiểm tra dateOfBirth (Ngày sinh không hợp lệ: Ngày sinh phải trước ngày hiện tại)
        if (user.getDateOfBirth() == null || user.getDateOfBirth().isAfter(LocalDate.now())) {
            errors.add("Dòng " + rowIndex + ": Ngày sinh không hợp lệ.");
        }

        // Kiểm tra streetName (Địa chỉ đường không được trống)
        if (user.getStreetName() == null || user.getStreetName().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tên đường bị trống.");
        }

        // Kiểm tra ward (Phường/xã không được trống)
        if (user.getWard() == null || user.getWard().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Phường/xã bị trống.");
        }

        // Kiểm tra district (Quận/huyện không được trống)
        if (user.getDistrict() == null || user.getDistrict().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Quận/huyện bị trống.");
        }

        // Kiểm tra province (Tỉnh thành không được trống)
        if (user.getProvince() == null || user.getProvince().isEmpty()) {
            errors.add("Dòng " + rowIndex + ": Tỉnh thành bị trống.");
        }

        // Kiểm tra yearsOfExperience (Số năm kinh nghiệm không hợp lệ: phải >= 0)
        if (user.getYearsOfExperience() == null || user.getYearsOfExperience() < 0) {
            errors.add("Dòng " + rowIndex + ": Số năm kinh nghiệm không hợp lệ.");
        }
    }
}
