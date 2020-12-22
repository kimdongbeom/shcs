package sinhan.custom.shcs.ExcelView;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import sinhan.custom.shcs.model.ResultExcel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ConvertedInvoiceXls")
public class InvoiceExcelView extends AbstractXlsView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Sheet sheet = workbook.createSheet("shcs");

        List<ResultExcel> dataList = (List<ResultExcel>) model.get("rows");

        Row row = sheet.createRow(0);
        Cell cell;

        //Colors
        CellStyle styleLimeColor = workbook.createCellStyle();
        styleLimeColor.setFillForegroundColor(IndexedColors.LIME.getIndex());
        styleLimeColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleGoldColor = workbook.createCellStyle();
        styleGoldColor.setFillForegroundColor(IndexedColors.GOLD.getIndex());
        styleGoldColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleBlueColor = workbook.createCellStyle();
        styleBlueColor.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        styleBlueColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell = row.createCell(0);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("인보이스번호");

        cell = row.createCell(1);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("제품코드");

        cell = row.createCell(2);
        cell.setCellStyle(styleGoldColor);
        cell.setCellValue("품명1");

        cell = row.createCell(3);
        cell.setCellStyle(styleGoldColor);
        cell.setCellValue("품명2");

        cell = row.createCell(4);
        cell.setCellStyle(styleGoldColor);
        cell.setCellValue("품명3");

        cell = row.createCell(5);
        cell.setCellStyle(styleGoldColor);
        cell.setCellValue("성분");

        cell = row.createCell(6);
        cell.setCellStyle(styleBlueColor);
        cell.setCellValue("품명1");

        cell = row.createCell(7);
        cell.setCellStyle(styleBlueColor);
        cell.setCellValue("품명2");

        cell = row.createCell(8);
        cell.setCellStyle(styleBlueColor);
        cell.setCellValue("품명3");

        cell = row.createCell(9);
        cell.setCellStyle(styleBlueColor);
        cell.setCellValue("성분");

        cell = row.createCell(10);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("수량");

        cell = row.createCell(11);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("수량단위");

        cell = row.createCell(12);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("단가");

        cell = row.createCell(13);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("금액");

        cell = row.createCell(14);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("원산지");

        cell = row.createCell(15);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("순중량");

        cell = row.createCell(16);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("HS Code");

        cell = row.createCell(17);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("포장단위");

        cell = row.createCell(18);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("포장수량");

        cell = row.createCell(19);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("상표명");

        cell = row.createCell(20);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("첨부여부");

        cell = row.createCell(21);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("FTA발급");

        cell = row.createCell(22);
        cell.setCellStyle(styleLimeColor);
        cell.setCellValue("회사명");

        /**
         * todo
         * 컨텐츠 내용이 3개이상 있을 경우 어떻게 처리할지 고민
         */
        int dataRowStartIndex = 1;
        DecimalFormat form = new DecimalFormat("#.##");
        for (ResultExcel data : dataList) {
            if (StringUtils.isNotBlank(data.getFiberContent4()) && StringUtils.isNotBlank(data.getFiberContent7())) {
                // set 9
                dataRowStartIndex = setFirstRow(sheet, dataRowStartIndex, form, data);

                dataRowStartIndex = setSecondLine(sheet, dataRowStartIndex, data, data.getFiberContent4(), data.getFiberContent5(), data.getFiberContent6());

                dataRowStartIndex = setThirdLine(sheet, dataRowStartIndex, data, data.getFiberContent7(), data.getFiberContent8(), data.getFiberContent9());
            } else if (StringUtils.isNotBlank(data.getFiberContent4())) {
                dataRowStartIndex = setFirstRow(sheet, dataRowStartIndex, form, data);

                dataRowStartIndex = setSecondLine(sheet, dataRowStartIndex, data, data.getFiberContent4(), data.getFiberContent5(), data.getFiberContent6());
            } else {
                dataRowStartIndex = setFirstRow(sheet, dataRowStartIndex, form, data);
            }
        }
    }

    private int setThirdLine(Sheet sheet, int dataRowStartIndex, ResultExcel data, String fiberContent7, String fiberContent8, String fiberContent9) {
        Row row;
        Cell cell;
        row = sheet.createRow(dataRowStartIndex);

        cell = row.createCell(0);
        cell.setCellValue(data.getInvoiceNo());

        cell = row.createCell(1);
        cell.setCellValue("");

        cell = row.createCell(2);
        cell.setCellValue("");

        cell = row.createCell(3);
        cell.setCellValue("");

        cell = row.createCell(4);
        cell.setCellValue("");

        cell = row.createCell(5);
        cell.setCellValue("");

        cell = row.createCell(6);
        cell.setCellValue(fiberContent7);

        cell = row.createCell(7);
        cell.setCellValue(fiberContent8);

        cell = row.createCell(8);
        cell.setCellValue(fiberContent9);

        cell = row.createCell(9);
        cell.setCellValue("");

        cell = row.createCell(10);
        cell.setCellValue("");

        cell = row.createCell(11);
        cell.setCellValue("");

        cell = row.createCell(12);
        cell.setCellValue("");

        cell = row.createCell(13);
        cell.setCellValue("");

        cell = row.createCell(14);
        cell.setCellValue(data.getOrigin());

        cell = row.createCell(15);
        cell.setCellValue("");

        cell = row.createCell(16);
        cell.setCellValue(data.getHsCode());

        cell = row.createCell(17);
        cell.setCellValue(data.getPackageUnit());

        cell = row.createCell(18);
        cell.setCellValue("");

        cell = row.createCell(19);
        cell.setCellValue("NO");

        cell = row.createCell(20);
        cell.setCellValue("");

        cell = row.createCell(21);
        cell.setCellValue("B");

        cell = row.createCell(22);
        cell.setCellValue(data.getCompanyName());

        dataRowStartIndex++;
        return dataRowStartIndex;
    }

    private int setSecondLine(Sheet sheet, int dataRowStartIndex, ResultExcel data, String fiberContent4, String fiberContent5, String fiberContent6) {
        Row row;
        Cell cell;
        row = sheet.createRow(dataRowStartIndex);

        cell = row.createCell(0);
        cell.setCellValue(data.getInvoiceNo());

        cell = row.createCell(1);
        cell.setCellValue("");

        cell = row.createCell(2);
        cell.setCellValue("");

        cell = row.createCell(3);
        cell.setCellValue("");

        cell = row.createCell(4);
        cell.setCellValue("");

        cell = row.createCell(5);
        cell.setCellValue("");

        cell = row.createCell(6);
        cell.setCellValue(fiberContent4);

        cell = row.createCell(7);
        cell.setCellValue(fiberContent5);

        cell = row.createCell(8);
        cell.setCellValue(fiberContent6);

        cell = row.createCell(9);
        cell.setCellValue("");

        cell = row.createCell(10);
        cell.setCellValue("");

        cell = row.createCell(11);
        cell.setCellValue("");

        cell = row.createCell(12);
        cell.setCellValue("");

        cell = row.createCell(13);
        cell.setCellValue("");

        cell = row.createCell(14);
        cell.setCellValue(data.getOrigin());

        cell = row.createCell(15);
        cell.setCellValue("");

        cell = row.createCell(16);
        cell.setCellValue(data.getHsCode());

        cell = row.createCell(17);
        cell.setCellValue(data.getPackageUnit());

        cell = row.createCell(18);
        cell.setCellValue("");

        cell = row.createCell(19);
        cell.setCellValue("NO");

        cell = row.createCell(20);
        cell.setCellValue("");

        cell = row.createCell(21);
        cell.setCellValue("B");

        cell = row.createCell(22);
        cell.setCellValue(data.getCompanyName());

        dataRowStartIndex++;
        return dataRowStartIndex;
    }

    private int setFirstRow(Sheet sheet, int dataRowStartIndex, DecimalFormat form, ResultExcel data) {
        Row row;
        Cell cell;
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
        cell.setCellValue(form.format(data.getCalculateWeight()));

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
        cell.setCellValue(data.getCompanyName());

        dataRowStartIndex++;
        return dataRowStartIndex;
    }
}
