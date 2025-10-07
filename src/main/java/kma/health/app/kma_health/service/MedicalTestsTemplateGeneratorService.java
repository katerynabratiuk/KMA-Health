package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.exception.ExaminationNotFoundException;
import kma.health.app.kma_health.repository.ExaminationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalTestsTemplateGeneratorService {

    private final ExaminationRepository examinationRepository;

    @Value("${ABSOLUTE_PATH}")
    private String absolutePath;

    public String generateTemplate(List<Long> testIds, String patientFullName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        createHeaderRow(sheet, headerStyle);
        fillDataRows(sheet, testIds, cellStyle);
        autoSizeColumns(sheet, 5);

        String filePath = buildFilePath(patientFullName);
        saveWorkbook(workbook, filePath);

        return filePath;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        setBorders(style);
        return style;
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        String[] headers = {"Аналіз", "Результат", "Одиниці", "Норма", "Коментар"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Sheet sheet, List<Long> testIds, CellStyle cellStyle) {
        int rowIndex = 1;
        for (Long id : testIds) {
            Optional<Examination> examOpt = examinationRepository.findById(id);
            if (examOpt.isEmpty()) throw new ExaminationNotFoundException("Could not find examination with id " + id);

            Examination exam = examOpt.get();
            Row row = sheet.createRow(rowIndex++);

            createCell(row, 0, exam.getExamName(), cellStyle);
            createCell(row, 1, "", cellStyle);
            createCell(row, 2, exam.getUnit(), cellStyle);
            createCell(row, 3, "", cellStyle);
            createCell(row, 4, "", cellStyle);
        }
    }

    private void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++)
            sheet.autoSizeColumn(i);
    }

    private String buildFilePath(String patientFullName) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeName = patientFullName.replaceAll("\\s+", "_");
        String fileName = safeName + "_" + timestamp + ".xlsx";
        return absolutePath + File.separator + fileName;
    }

    private void saveWorkbook(Workbook workbook, String filePath) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save Excel file: " + filePath, e);
        }
    }
}
