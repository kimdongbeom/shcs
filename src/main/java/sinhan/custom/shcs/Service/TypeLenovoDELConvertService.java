package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import sinhan.custom.shcs.model.Lenovo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TypeLenovoDELConvertService {

    public List<Lenovo> convertTypeLenovoDEL(File file) {
        int invoiceOrder = 1;

        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");

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
                                        break;
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
                                data.setInvoiceTotalAmount(Double.valueOf(totalValueOfGoods.trim().replace(",", "")));
                            }
                        }
                    }

                    if (line.contains("TOTAL GROSS:")) {
                        String totalGross = line.split("TOTAL GROSS:")[1];
                        for (Lenovo data : resultDataList) {
                            if (data.getTotalGrossWeight() == null) {
                                String totalGrossWeight = totalGross.trim().split(" ")[0].replace(",", "");
                                data.setTotalGrossWeight(Double.valueOf(totalGrossWeight));
                            }
                        }
                    }

                }
            }
            document.close();
            return resultDataList;
        } catch (Exception e) {
            log.error("Pdf Convert Error [TYPE_PCS]", e);
        }
        return null;
    }
}
