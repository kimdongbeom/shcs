package sinhan.custom.shcs.main;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import sinhan.custom.shcs.Service.ExcelServicePcs;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainPdfToExcelTypePcs {

    public static int invoiceOrder = 1;

    public static void main(String[] args) {
        try {
//            File file = new File("/Users/nhnent/Desktop/S8R0012857.pdf");
//            File file = new File("/Users/nhnent/Desktop/shcs/RBO500291.pdf");
//            File file = new File("/Users/nhnent/Desktop/shcs/0917_PEN9040664_YO.pdf");
            File file = new File("/Users/nhnent/Desktop/shcs/0917_PEN9040664_YO.pdf");
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
//            pdfStripper.setStartPage(3);
//            pdfStripper.setEndPage(3);

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");
            List<PDFExtractData> dataList = new ArrayList<>();

            int invoiceNo = 0;
            boolean isTargetBlock = false;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                if (line.startsWith("Invoice No.")) {
                    String[] splitLine = line.split("Invoice No.");
                    if (splitLine.length == 2) {
                        String invoice = splitLine[1].replaceAll(" ", "");
                        if (StringUtils.isNumeric(invoice)) {
                            int newInvoiceNo = Integer.parseInt(invoice);

                            if (invoiceNo != newInvoiceNo) {
                                invoiceOrder = 1;
                            }
                            invoiceNo = newInvoiceNo;
                        }
                    }
                }

                if (line.startsWith("Delivery no.") && line.contains("Material no.")) {
                    isTargetBlock = true;
                }

                if (line.contains("Sub total")) {
                    isTargetBlock = false;
                }

                if (isTargetBlock) {
                    if (line.contains("pcs")) {
                        String materialNo = "";
                        String pcs = "";

                        String[] splitTargetForMaterialNo = line.split(" ");

                        int pcsCount = 0;
                        for (String word : splitTargetForMaterialNo) {
                            if (word.contains("pcs")) {
                                pcsCount++;
                            }
                        }

                        if (pcsCount == 1) {
                            String targetForPcs = line.split("pcs")[0];
                            setExcelData(lines[i+1], invoiceNo, dataList, splitTargetForMaterialNo, targetForPcs);
                        } else if (pcsCount == 2){
                            // ex : "14.880 2.608.577.147 Case L - 35pcs Multi VN 82079030 Accessory Set  24 pcs"; => pcs가 두 번 들어 있음
                            String targetForPcs = line.split("pcs")[1].split("pcs")[0];
                            setExcelData(lines[i+1], invoiceNo, dataList, splitTargetForMaterialNo, targetForPcs);
                        }
                    }
                }
            }

            ExcelServicePcs excelService = new ExcelServicePcs();
            excelService.excelWrite(dataList, "/Users/nhnent/Desktop/shcs/0917_PEN9040664_YO_result.xls");
        } catch(Exception e) {
            log.error("Pdf Convert Error [TYPE1]", e);
        }
    }

    private static void setExcelData(String nextLine, int invoiceNo, List<PDFExtractData> dataList, String[] splitTargetForMaterialNo, String targetForPcs) {
        // nextLine is For extracting Origin
        String pcs;
        String materialNo;
        String[] splitTargetForPcs = targetForPcs.split(" ");
        pcs = splitTargetForPcs[splitTargetForPcs.length - 1].replace(",", "");
        for (String target : splitTargetForMaterialNo) {
            if (target.length() == 13 && target.contains(".")) {
//                String origin = extractOrigin(nextLine);
                materialNo = target.replace(".", "");
                PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, materialNo, Double.valueOf(pcs), origin);
                dataList.add(pdfExtractData);
            }
        }
    }

    private static String extractOrigin(String nextLine) {
        String[] splitLine = nextLine.split(" ");
        String origin = splitLine[1];
        if (origin.length() == 2) {
            return origin;
        }
        return "No Origin";
    }
}
