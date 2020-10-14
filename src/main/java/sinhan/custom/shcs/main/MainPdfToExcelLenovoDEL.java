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
//            File file = new File("/Users/nhnent/Desktop/shcs/lenova/ZHINC5502404_IV.PDF");
            File file = new File("/Users/nhnent/Desktop/shcs/lenova/CI-RORO-COHEAH667EC507.pdf");
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            //load all lines into a string
            String pages = pdfStripper.getText(document);

            //split by detecting newline
            String[] lines = pages.split("\r\n|\r|\n");
            List<PDFExtractData> dataList = new ArrayList<>();

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

                            productIdentification = lines[i+1].trim();
                            String[] next2LineWords = lines[i+2].split(" ");

                            if (next2LineWords.length == 7) {
                                lenovo.setProductDescription(lines[i+1].trim());
                                lenovo.splitLineDataDEL(lines[i+2]);
                            } else {
                                isChangedPage = true;
                                for (int j=i+2; j < lines.length; j++) {
                                    String[] nextLine = lines[j].split(" ");
                                    if (nextLine.length == 7) {
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
