package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Slf4j
@Service
public class ExcelService {

    public void excelWrite(List<PDFExtractData> dataList, String outputPath) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("code");

        cell = row.createCell(1);
        cell.setCellValue("InvoiceOrder");

        cell = row.createCell(2);
        cell.setCellValue("InvoiceNo");

        cell = row.createCell(3);
        cell.setCellValue("MaterialId");

        cell = row.createCell(4);
        cell.setCellValue("E");

        cell = row.createCell(5);
        cell.setCellValue("Quantity");

        int dataRowStartIndex = 1;
        for (PDFExtractData data : dataList) {
            row = sheet.createRow(dataRowStartIndex);

            cell = row.createCell(0);
            cell.setCellValue(5002);

            cell = row.createCell(1);
            cell.setCellValue(data.getInvoiceOrder());

            cell = row.createCell(2);
            cell.setCellValue(data.getInvoiceNo());

            cell = row.createCell(3);
            cell.setCellValue(data.getMaterialNo());

            cell = row.createCell(4);
            cell.setCellValue("");

            cell = row.createCell(5);
            cell.setCellValue(data.getQuantity());

            dataRowStartIndex ++;
        }

        makeExcelFile(outputPath, workbook);
    }

    private void makeExcelFile(String outputPath, HSSFWorkbook workbook) {
        File file = new File(outputPath);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (Exception e) {
            log.error("Write Excel Error", e);
        }
    }

}
