package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TypePCSConvertService {

    public List<PDFExtractData> convertTypePCS (File file) {
        int invoiceOrder=1;

        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();

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
                            String[] splitTargetForPcs = targetForPcs.split(" ");
                            pcs = splitTargetForPcs[splitTargetForPcs.length - 1].replace(",", "");
                            for (String target : splitTargetForMaterialNo) {
                                if (target.length() == 13 && target.contains(".")) {
                                    materialNo = target.replace(".", "");
                                    PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, materialNo, Double.valueOf(pcs));
                                    dataList.add(pdfExtractData);
                                    invoiceOrder += 1;
                                }
                            }
                        } else if (pcsCount == 2){
                            // ex : "14.880 2.608.577.147 Case L - 35pcs Multi VN 82079030 Accessory Set  24 pcs"; => pcs가 두 번 들어 있음
                            String targetForPcs = line.split("pcs")[1].split("pcs")[0];
                            String[] splitTargetForPcs = targetForPcs.split(" ");
                            pcs = splitTargetForPcs[splitTargetForPcs.length - 1].replace(",", "");
                            for (String target : splitTargetForMaterialNo) {
                                if (target.length() == 13 && target.contains(".")) {
                                    materialNo = target.replace(".", "");
                                    PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, materialNo, Double.valueOf(pcs));
                                    dataList.add(pdfExtractData);
                                    invoiceOrder += 1;
                                }
                            }
                        }
                    }
                }
            }
            return dataList;
        } catch (Exception e) {
            log.error("Pdf Convert Error [TYPE_PCS]", e);
        }
        return null;
    }
}
