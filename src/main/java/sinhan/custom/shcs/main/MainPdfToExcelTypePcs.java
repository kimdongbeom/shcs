package sinhan.custom.shcs.main;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import sinhan.custom.shcs.Service.ExcelService;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MainPdfToExcelTypePcs {

    public static void main(String[] args) {
        try {
            File file = new File("/Users/nhnent/Desktop/shcs/S8R0013325.pdf");
//            File file = new File("/Users/nhnent/Desktop/shcs/RBO500291.pdf");
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
                            setExcelData(dataList, splitTargetForMaterialNo, targetForPcs);
                        } else if (pcsCount == 2){
                            // ex : "14.880 2.608.577.147 Case L - 35pcs Multi VN 82079030 Accessory Set  24 pcs"; => pcs가 두 번 들어 있음
                            String targetForPcs = line.split("pcs")[1].split("pcs")[0];
                            setExcelData(dataList, splitTargetForMaterialNo, targetForPcs);
                        }
                    }
                }
            }

            ExcelService excelService = new ExcelService();
            excelService.excelWrite(dataList, "/Users/nhnent/Desktop/S8R0013325.xls");
        } catch(Exception e) {
            log.error("Pdf Convert Error [TYPE1]", e);
        }
    }

    private static void setExcelData(List<PDFExtractData> dataList, String[] splitTargetForMaterialNo, String targetForPcs) {
        String pcs;
        String materialNo;
        String[] splitTargetForPcs = targetForPcs.split(" ");
        pcs = splitTargetForPcs[splitTargetForPcs.length - 1].replace(",", "");
        for (String target : splitTargetForMaterialNo) {
            if (target.length() == 13 && target.contains(".")) {
                materialNo = target.replace(".", "");
                PDFExtractData pdfExtractData = new PDFExtractData(materialNo, Double.valueOf(pcs));
                dataList.add(pdfExtractData);
            }
        }
    }
}
