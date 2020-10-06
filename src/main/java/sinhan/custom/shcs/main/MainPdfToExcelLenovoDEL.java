package sinhan.custom.shcs.main;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainPdfToExcelLenovoDEL {

    private static int invoiceOrder = 1;

    public static void main(String[] args) {


        try {
            File file = new File("/Users/nhnent/Desktop/shcs/lenova/ZHINC5502404_IV.PDF");
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
            String invoiceNo = "";
            for (int i=0; i < lines.length; i++) {
                String line = lines[i];

                if (line.contains("INVOICE NO.")) {
                    invoiceNo = lines[i+1];
                }

                if (line.startsWith("DEL NO. DESCRIPTION")) {
                    isTargetBlock = true;
                }

                if (line.contains("Remarks:")) {
                    isTargetBlock = false;
                }

                if (isTargetBlock) {

                    if (line.startsWith("(00)SSCC#")) {
                        String[] splitLine = line.split(" ");
                        if (splitLine.length == 3 && splitLine[2].length() == 18) {
                            lenovo.setProductDescription(lines[i+1].trim());
                            String[] next2LineWords = lines[i+2].split(" ");
                            if (next2LineWords.length == 7 && next2LineWords[0].length() == 10) {
                                lenovo.splitLineDataDEL(lines[i+2]);
                            } else {
                                for (int j=i+2; j < lines.length; j++) {
                                    String[] nextLine = lines[j].split(" ");
                                    if (nextLine.length == 7 && nextLine[0].length() == 10) {
                                        lenovo.splitLineDataDEL(lines[j]);
                                    }
                                }
                            }
                            lenovo.setInvoiceNo(invoiceNo);
                            resultDataList.add(lenovo);
                            lenovo = new Lenovo();
                        }
                    }

                    if (line.contains("INVOICE TOTAL AMOUNT")) {
                        String totalValueOfGoods = line.split("INVOICE TOTAL AMOUNT ")[1];
                        for (Lenovo data : resultDataList) {
                            if (data.getInvoiceTotalAmount() == null) {
                                data.setInvoiceTotalAmount(totalValueOfGoods.trim());
                            }
                        }
                    }

                    if (line.contains("TOTAL GROSS:")) {
                        String totalGross = line.split("TOTAL GROSS:")[1];
                        for (Lenovo data : resultDataList) {
                            if (data.getTotalGrossWeight() == null) {
                                data.setTotalGrossWeight(totalGross.split(" ")[0]);
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
