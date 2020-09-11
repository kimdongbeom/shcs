package sinhan.custom.shcs.main;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import sinhan.custom.shcs.Service.ExcelService;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainPdfToExcelTypeEa {

    public static void main(String[] args) {
        try {
            // todo args에서
//            File file = new File("/Users/nhnent/Desktop/shcs/S8R0013549.pdf");
            File file = new File("/Users/nhnent/Desktop/S8R0013915.pdf");
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
//            pdfStripper.setStartPage(3);
//            pdfStripper.setEndPage(3);

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");
            List<PDFExtractData> dataList = new ArrayList<>();

            boolean isTargetBlock = false;
            for (String line : lines) {
                if (line.startsWith("Pos-") && line.contains("Material No. Idx")) {
                    isTargetBlock = true;
                }

                if (line.startsWith("Del.No.")) {
                    isTargetBlock = true;
                }

                if (line.contains("Location:") || line.contains("Transport:") || line.contains("Shipping:")) {
                    isTargetBlock = false;
                }

                if (isTargetBlock) {
                    String materialNo = "";
                    String EA = "";
                    if (line.startsWith("Cust.") == false && line.contains("EA")) {
                        String[] splitTargetForMaterialNo = line.split(" ");

                        int eaCount = 0;
                        for (String word : splitTargetForMaterialNo) {
                            if (word.contains("EA")) {
                                eaCount++;
                            }
                        }

                        if (eaCount == 1) {
                            String targetForEA = line.split("EA")[0];
                            String[] splitTargetForEA = targetForEA.split(" ");
                            if (splitTargetForEA[0].length() == 5 && StringUtils.isNumeric(splitTargetForEA[0])) {
                                setExcelData(dataList, splitTargetForMaterialNo, splitTargetForEA);
                            }
                        } else {
                            // ex : 00150 0.250.603.006.EAF glow plug     20 EA    8,76 *     175,20
                            String targetForEA = line.split("EA")[1].split("EA")[0];
                            String[] splitTargetForEA = targetForEA.split(" ");
                            if (splitTargetForEA[0].startsWith("F")) {
                                setExcelData(dataList, splitTargetForMaterialNo, splitTargetForEA);
                            }
                        }
                    } else if (line.contains("SET")) {
                        // ex : 00060 3.397.118.938.KC0 Set Of Wiper Blades    395 SET    8,83 *    3.487,85 X
                        String[] splitTargetForMaterialNo = line.split(" ");
                        String targetForEA = line.split("SET")[0];
                        String[] splitTargetForEA = targetForEA.split(" ");
                        if (splitTargetForEA[0].length() == 5 && StringUtils.isNumeric(splitTargetForEA[0])) {
                            setExcelData(dataList, splitTargetForMaterialNo, splitTargetForEA);
                        }
                    }
                }
            }

            ExcelService excelService = new ExcelService();
            excelService.excelWrite(dataList, "/Users/nhnent/Desktop/S8R0013915_excel333.xls");
        } catch(Exception e) {
            log.error("Pdf Convert Error [TYPE1]", e);
        }
    }

    private static void setExcelData(List<PDFExtractData> dataList, String[] splitTargetForMaterialNo, String[] splitTargetForEA) {
        String EA;
        String materialNo;
        EA = splitTargetForEA[splitTargetForEA.length - 1];
        for (String target : splitTargetForMaterialNo) {
            if (target.length() == 17 && target.contains(".")) {
                materialNo = target.replace(".", "");
                PDFExtractData pdfExtractData = new PDFExtractData(materialNo, Double.valueOf(EA));
                dataList.add(pdfExtractData);
            }
        }
    }
}
