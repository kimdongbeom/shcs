package sinhan.custom.shcs.ExcelView;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import sinhan.custom.shcs.model.ResultExcel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ConvertedInvoiceXls")
public class InvoiceExcelView extends AbstractXlsView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ResultExcel> dataList = (List<ResultExcel>) model.get("rows");

        Sheet sheet = workbook.createSheet("shcs");
        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("인보이스번호");

        cell = row.createCell(1);
        cell.setCellValue("제품코드");

        cell = row.createCell(2);
        cell.setCellValue("품명1");

        cell = row.createCell(3);
        cell.setCellValue("품명1");

        cell = row.createCell(4);
        cell.setCellValue("품명3");

        cell = row.createCell(5);
        cell.setCellValue("성분");

        cell = row.createCell(6);
        cell.setCellValue("품명1");

        cell = row.createCell(7);
        cell.setCellValue("품명2");

        cell = row.createCell(8);
        cell.setCellValue("품명3");

        cell = row.createCell(9);
        cell.setCellValue("성분");

        cell = row.createCell(10);
        cell.setCellValue("수량");

        cell = row.createCell(11);
        cell.setCellValue("수량단위");

        cell = row.createCell(12);
        cell.setCellValue("단가");

        cell = row.createCell(13);
        cell.setCellValue("금액");

        cell = row.createCell(14);
        cell.setCellValue("원산지");

        cell = row.createCell(15);
        cell.setCellValue("순중량");

        cell = row.createCell(16);
        cell.setCellValue("HS Code");

        cell = row.createCell(17);
        cell.setCellValue("포장단위");

        cell = row.createCell(18);
        cell.setCellValue("포장수량");

        cell = row.createCell(19);
        cell.setCellValue("상표명");

        cell = row.createCell(20);
        cell.setCellValue("첨부여부");

        cell = row.createCell(21);
        cell.setCellValue("FTA발급");

        cell = row.createCell(22);
        cell.setCellValue("회사명");

        /**
         * todo
         * 컨텐츠 내용이 3개이상 있을 경우 어떻게 처리할지 고민
         */
        int dataRowStartIndex = 1;
        for (ResultExcel data : dataList) {
            row = sheet.createRow(dataRowStartIndex);

            cell = row.createCell(0);
            cell.setCellValue(data.getInvoiceNo());

            cell = row.createCell(1);
            cell.setCellValue(data.getProductCode());

            cell = row.createCell(2);
            cell.setCellValue(data.getProductName1());

            cell = row.createCell(3);
            cell.setCellValue(data.getProductName2());

            cell = row.createCell(4);
            cell.setCellValue(data.getProductName3());

            cell = row.createCell(5);
            cell.setCellValue(data.getFabric());

            cell = row.createCell(6);
            cell.setCellValue(data.getFiberContent1());

            cell = row.createCell(7);
            cell.setCellValue(data.getFiberContent2());

            cell = row.createCell(8);
            cell.setCellValue(data.getFiberContent3());

            cell = row.createCell(9);
            cell.setCellValue("");

            cell = row.createCell(10);
            cell.setCellValue(data.getCount());

            cell = row.createCell(11);
            cell.setCellValue(data.getUnit());

            cell = row.createCell(12);
            cell.setCellValue(data.getUnitPrice());

            cell = row.createCell(13);
            cell.setCellValue(data.getTotalPrice());

            cell = row.createCell(14);
            cell.setCellValue(data.getOrigin());

            cell = row.createCell(15);
            cell.setCellValue(data.getCalculateWeight());
//            if (data.getCalculateWeight() == 0.0) {
//                cell.setCellValue("");
//            } else {
//                cell.setCellValue(data.getCalculateWeight());
//            }

            cell = row.createCell(16);
            cell.setCellValue(data.getHsCode());

            cell = row.createCell(17);
            cell.setCellValue(data.getPackageUnit());

            cell = row.createCell(18);
            cell.setCellValue(data.getPackageCount());

            cell = row.createCell(19);
            cell.setCellValue("NO");

            cell = row.createCell(20);
            cell.setCellValue("");

            cell = row.createCell(21);
            cell.setCellValue("B");

            cell = row.createCell(22);
            cell.setCellValue("회사명");

            dataRowStartIndex++;
        }
    }
}
