package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PdfConvertService {

    public List<PDFExtractData> makeExcelFileUsingUploadedPdf(MultipartFile multipartFile) {
        File file = convertFile(multipartFile);
        return convertType1(file);
    }

    private File convertFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public List<PDFExtractData> convertType1 (File file) {
        List<PDFExtractData> dataList = new ArrayList<>();
        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
//            pdfStripper.setStartPage(3);
//            pdfStripper.setEndPage(3);

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");


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
                        } else {
                            String targetForPcs = line.split("pcs")[1].split("pcs")[0];
                            setExcelData(dataList, splitTargetForMaterialNo, targetForPcs);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Pdf Convert Error [TYPE1]", e);
        }
        return dataList;
    }

    private void setExcelData(List<PDFExtractData> dataList, String[] splitTargetForMaterialNo, String targetForPcs) {
        String pcs;
        String materialNo;
        String[] splitTargetForPcs = targetForPcs.split(" ");
        pcs = splitTargetForPcs[splitTargetForPcs.length - 1].replace(",", "");
        for (String target : splitTargetForMaterialNo) {
            if (target.length() == 13 && target.contains(".")) {
                materialNo = target.replace(".", "");
//                PDFExtractData pdfExtractData = new PDFExtractData(materialNo, Double.valueOf(pcs));
//                dataList.add(pdfExtractData);
            }
        }
    }
}
