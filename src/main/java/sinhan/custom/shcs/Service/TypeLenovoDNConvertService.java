package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TypeLenovoDNConvertService {

    public List<Lenovo> convertTypeLenovoDN(File file) {
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
            for (int i = 0; i < lines.length; i++) {
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
                                data.setInvoiceTotalAmount(totalValueOfGoods.trim());
                            }
                        }
                        if (lenovo.getHtsCode() == null) {
                            lenovo.setHtsCode(lines[i+1]);
                        }
                    }

                    if (line.contains("TOTAL GROSS WEIGHT:")) {
                        for (Lenovo data : resultDataList) {
                            if (data.getTotalGrossWeight() == null) {
                                data.setTotalGrossWeight(lines[i+2]);
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
