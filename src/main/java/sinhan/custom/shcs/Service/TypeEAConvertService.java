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
public class TypeEAConvertService {

    public List<PDFExtractData> convertTypeEA (File file) {
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
            for (String line : lines) {

                if (line.contains("Document No.:")) {
                    String[] splitLine = line.split("Document No.:");
                    if (splitLine.length == 2) {
                        String invoice = splitLine[1].replaceAll(" ", "");
                        if (StringUtils.isNumeric(invoice)) {
                            invoiceNo = Integer.parseInt(invoice);
                        }
                    }
                }

                if (line.contains("PKI,SPP=ISO-Palett")) {
                    invoiceOrder = 1;
                    invoiceNo = 0;
                }

//                if (line.contains("Amount of C")) {
                if (line.startsWith("Pos") && line.contains("Material No. Idx")) {
                    isTargetBlock = true;
                }

                if (line.startsWith("Del.No.") || ((line.startsWith("Your order")) && line.contains("Order no"))) {
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
                            if (StringUtils.isNumeric(splitTargetForEA[0])) {
                                EA = splitTargetForEA[splitTargetForEA.length - 1];

                                if (StringUtils.isNumeric(EA)) {
                                    for (String target : splitTargetForMaterialNo) {
                                        if ((target.length() == 17) && target.contains(".")) {
                                            materialNo = target.replace(".", "");
                                            PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder, invoiceNo, materialNo, Double.valueOf(EA));
                                            dataList.add(pdfExtractData);
                                            invoiceOrder += 1;
                                        } else if (target.length() == 13) {
                                            PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, target, Double.valueOf(EA));
                                            dataList.add(pdfExtractData);
                                            invoiceOrder += 1;
                                        }
                                    }
                                }
                            }
                        } else {
                            // ex : 00150 0.250.603.006.EAF glow plug     20 EA    8,76 *     175,20
                            String targetForEA = line.split("EA")[1].split("EA")[0];
                            String[] splitTargetForEA = targetForEA.split(" ");
                            if (splitTargetForEA[0].startsWith("F")) {
                                EA = splitTargetForEA[splitTargetForEA.length - 1];

                                if (StringUtils.isNumeric(EA)) {
                                    for (String target : splitTargetForMaterialNo) {
                                        if ((target.length() == 17) && target.contains(".")) {
                                            materialNo = target.replace(".", "");
                                            PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder, invoiceNo, materialNo, Double.valueOf(EA));
                                            dataList.add(pdfExtractData);
                                            invoiceOrder += 1;
                                        } else if (target.length() == 13) {
                                            PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, target, Double.valueOf(EA));
                                            dataList.add(pdfExtractData);
                                            invoiceOrder += 1;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (line.contains("SET")) {
                        // ex : 00060 3.397.118.938.KC0 Set Of Wiper Blades    395 SET    8,83 *    3.487,85 X
                        String[] splitTargetForMaterialNo = line.split(" ");
                        String targetForEA = line.split("SET")[0];
                        String[] splitTargetForEA = targetForEA.split(" ");
                        if (StringUtils.isNumeric(splitTargetForEA[0])) {
                            EA = splitTargetForEA[splitTargetForEA.length - 1];

                            if (StringUtils.isNumeric(EA)) {
                                for (String target : splitTargetForMaterialNo) {
                                    if ((target.length() == 17) && target.contains(".")) {
                                        materialNo = target.replace(".", "");
                                        PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder, invoiceNo, materialNo, Double.valueOf(EA));
                                        dataList.add(pdfExtractData);
                                        invoiceOrder += 1;
                                    } else if (target.length() == 13) {
                                        PDFExtractData pdfExtractData = new PDFExtractData(invoiceOrder,invoiceNo, target, Double.valueOf(EA));
                                        dataList.add(pdfExtractData);
                                        invoiceOrder += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return dataList;
        } catch(Exception e) {
            log.error("Pdf Convert Error [TYPE1]", e);
        }
        return null;
    }
}
