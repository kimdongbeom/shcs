package sinhan.custom.shcs.ExcelView;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import sinhan.custom.shcs.model.PDFExtractData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ConvertedXls")
public class ExcelView extends AbstractXlsView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<PDFExtractData> dataList = (List<PDFExtractData>) model.get("rows");

        Sheet sheet = workbook.createSheet("shcs");
        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("MaterialId");

        cell = row.createCell(1);
        cell.setCellValue("Quantity");

        int dataRowStartIndex = 1;
        for (PDFExtractData data : dataList) {
            row = sheet.createRow(dataRowStartIndex);

            cell = row.createCell(0);
            cell.setCellValue(data.getMaterialNo());

            cell = row.createCell(1);
            cell.setCellValue(data.getQuantity());

            dataRowStartIndex ++;
        }
    }
}
