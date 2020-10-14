package sinhan.custom.shcs.ExcelView;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ConvertedLenovoXls")
public class LenovoExcelView extends AbstractXlsView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Lenovo> dataList = (List<Lenovo>) model.get("rows");

        Sheet sheet = workbook.createSheet("shcs");
        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("HTS CODE");

        cell = row.createCell(1);
        cell.setCellValue("PRODUCT IDENTIFICATION");

        cell = row.createCell(2);
        cell.setCellValue("PRODUCT DESCRIPTION");

        cell = row.createCell(3);
        cell.setCellValue("QUANTITY");

        cell = row.createCell(4);
        cell.setCellValue("UOM");

        cell = row.createCell(5);
        cell.setCellValue("UNIT PRICE");

        cell = row.createCell(6);
        cell.setCellValue("AMOUNT");

        cell = row.createCell(7);
        cell.setCellValue("I");

        cell = row.createCell(8);
        cell.setCellValue("INVOICE NO");

        cell = row.createCell(9);
        cell.setCellValue("INVOICE TOTAL AMOUNT");

        cell = row.createCell(10);
        cell.setCellValue("TOTAL GROSS WEIGHT");

        int dataRowStartIndex = 1;
        for (Lenovo data : dataList) {
            row = sheet.createRow(dataRowStartIndex);

            cell = row.createCell(0);
            cell.setCellValue(data.getHtsCode());

            cell = row.createCell(1);
            cell.setCellValue(data.getProductIdentification() + "(" + data.getCtryOfOrigin() + ")");

            cell = row.createCell(2);
            cell.setCellValue(data.getProductDescription());

            cell = row.createCell(3);
            cell.setCellValue(Integer.parseInt(data.getQuantity()));

            cell = row.createCell(4);
            cell.setCellValue(data.getUom());

            cell = row.createCell(5);
            cell.setCellValue(data.getUnitPrice());

            cell = row.createCell(6);
            cell.setCellValue(data.getAmount());

            cell = row.createCell(7);
            cell.setCellValue("");

            cell = row.createCell(8);
            cell.setCellValue(data.getInvoiceNo());

            cell = row.createCell(9);
            cell.setCellValue(data.getInvoiceTotalAmount());

            cell = row.createCell(10);
            cell.setCellValue(data.getTotalGrossWeight());

            dataRowStartIndex ++;
        }
    }
}
