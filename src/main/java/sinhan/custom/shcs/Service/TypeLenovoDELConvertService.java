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
            boolean isSameBlock = true;
            boolean isChangedPage = false;
            String productIdentification = "";
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
                    isSameBlock = true;
                }

                if (line.contains("Remarks:")) {
                    isTargetBlock = false;
                }

                if (line.contains("INVOICE TOTAL AMOUNT")) {
                    isSameBlock = false;
                }

                if (isTargetBlock) {

                    if (line.startsWith("(00)SSCC#")) {
                        String[] splitLine = line.split(" ");

                        if (splitLine.length == 3 && splitLine[2].length() == 18) {


                            String[] next2LineWords = lines[i+2].split(" ");

                            if (next2LineWords.length == 7 && !next2LineWords[next2LineWords.length - 1].equals("DIMENSIONS") && StringUtils.isNotBlank(next2LineWords[0])) {
                                lenovo.setProductDescription(lines[i+1].trim());
                                lenovo.splitLineDataDEL(lines[i+2]);
                            } else {
                                isChangedPage = true;
                                for (int j=i+2; j < lines.length; j++) {
                                    String[] nextLine = lines[j].split(" ");
                                    if (nextLine.length == 7 && !nextLine[nextLine.length - 1].equals("DIMENSIONS") && StringUtils.isNotBlank(nextLine[0])) {
                                        productIdentification = lines[j-1].trim();
                                        if (productIdentification.equals("FCA Free carrier") || productIdentification.equals("FOB Free on board")) {
                                            productIdentification = lines[i+1].trim();
                                        }
                                        lenovo.splitLineDataDEL(lines[j]);
                                        if (isSameBlock) {
                                            // 페이지가 바뀌는 부분이라서 이전 데이터의 description과 동일하기 떄문에 동일한 값을 넣어준다.
                                            if (isChangedPage) {
                                                if (productIdentification.contains("LENOVO KOREA LLC")) {
                                                    lenovo.setProductDescription(lines[j-1].trim());
                                                } else {
                                                    lenovo.setProductDescription(productIdentification);
                                                    isChangedPage = false;
                                                }
                                            } else {
                                                int listSize = resultDataList.size() - 1;
                                                lenovo.setProductDescription(resultDataList.get(listSize).getProductDescription());
                                            }
                                        } else {
                                            lenovo.setProductDescription(lines[j+3].trim());
                                        }
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
