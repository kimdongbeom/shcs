package sinhan.custom.shcs.main;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import sinhan.custom.shcs.Service.ExcelService;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainPdfToExcelLenovoDN {

    private static int invoiceOrder = 1;

    public static void main(String[] args) {


        try {
            File file = new File("/Users/nhnent/Desktop/shcs/lenova/41100105002030 IV.PDF");
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");
            List<PDFExtractData> dataList = new ArrayList<>();

            boolean isTargetBlock = false;
            List<Lenovo> resultDataList = new ArrayList<>();
            Lenovo lenovo = new Lenovo();
            for (int i=0; i < lines.length; i++) {
                String line = lines[i];

                if (line.contains("DN REF") && line.contains("INVOICE REF")) {
                    isTargetBlock = true;
                }

                if (line.contains("CERTIFIED TRUE AND CORRECT") && line.contains("PC HK")) {
                    isTargetBlock = false;
                }

                if (isTargetBlock) {
                    String[] splitLine = line.split(" ");
                    if (StringUtils.isNumeric(splitLine[0]) && splitLine[0].length() == 10 && splitLine.length == 2) {
                        String[] splitTargetForMaterialNo = line.split(" ");
                        lenovo.setInvoiceNo(lines[i+1]);
                        lenovo.splitLineDataDN(lines[i+4]);

                        resultDataList.add(lenovo);
                        lenovo = new Lenovo();
                    }

                    if (line.contains("TOTAL VALUE OF GOODS USD")) {
                        String totalValueOfGoods = line.split("TOTAL VALUE OF GOODS USD ")[1];
                        for (Lenovo data : resultDataList) {
                            if (data.getInvoiceTotalAmount() == null) {
                                data.setInvoiceTotalAmount(Double.valueOf(totalValueOfGoods.trim().replace(",", "")));
                                if (data.getHtsCode() == null) {
                                    data.setHtsCode(lines[i+1]);
                                }
                            }
                        }
                    }

                    if (line.contains("TOTAL GROSS WEIGHT:")) {
                        for (Lenovo data : resultDataList) {
                            if (data.getTotalGrossWeight() == null) {
                                String totalGrossWeight = lines[i+2].trim().split(" ")[0].replace(",", "");
                                data.setTotalGrossWeight(Double.valueOf(totalGrossWeight));
                            }
                        }
                    }
                }
            }
            System.out.println(resultDataList.size());
        } catch (Exception e) {
            log.error("Pdf Convert Error [Lenovo DN Type]", e);
        }

    }

    private static void setExcelData(String line, int invoiceNo, List<PDFExtractData> dataList, String[] splitTargetForMaterialNo, String[] splitTargetForEA) {
        String EA;
        String materialNo;
        EA = splitTargetForEA[splitTargetForEA.length - 1];

        if (StringUtils.isNumeric(EA)) {
            for (String target : splitTargetForMaterialNo) {
                if ((target.length() == 17) && target.contains(".")) {
                    materialNo = target.replace(".", "");
                    PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder, invoiceNo, materialNo, Double.valueOf(EA));
                    dataList.add(pdfExtractData);
                    invoiceOrder += 1;
                } else if (target.length() == 13) {
                    PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder, invoiceNo, target, Double.valueOf(EA));
                    dataList.add(pdfExtractData);
                    invoiceOrder += 1;
                }
            }
        }
    }
}
