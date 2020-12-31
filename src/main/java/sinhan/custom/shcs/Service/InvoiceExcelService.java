package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sinhan.custom.shcs.model.ExcelColumn;
import sinhan.custom.shcs.model.Material;
import sinhan.custom.shcs.model.Packing;
import sinhan.custom.shcs.model.ResultExcel;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class InvoiceExcelService {

    @Value("${da.pdf.upload.dir}")
    private String daPdfUploadDir;

    public List<ResultExcel> convertExcelToResultModel(MultipartFile multipartFile) {
        List<ResultExcel> resultExcelList = new ArrayList<>();
        String invoiceNo = "";
        try {
            File file = new File(multipartFile.getOriginalFilename());

//            InputStream inputStream = new FileInputStream(file);
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowItr = sheet.iterator();
            boolean isMatrialRange = false;
            boolean isPackingRange = false;

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(3).getPhysicalNumberOfCells();

            List<ExcelColumn> materialList = new ArrayList<>();
            List<ExcelColumn> packingList = new ArrayList<>();
            String ctNo = "";

            breakOut:
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {

                    Cell currentCell = sheet.getRow(i).getCell(j);

                    DataFormatter formatter = new DataFormatter();

                    String cellValue = formatter.formatCellValue(currentCell);
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (cellValue.contains("(8)No. & date of invoice")) {
                            Cell cell = sheet.getRow(i + 1).getCell(j);
                            invoiceNo = formatter.formatCellValue(cell);
                        } else if (cellValue.contains("MATERIAL FOR KNITTED")) {
                            isMatrialRange = true;
                        } else if (cellValue.contains("PACKING ACC")) {
                            isMatrialRange = false;
                            isPackingRange = true;
                        } else if (cellValue.contains("TOTAL NET WEIGHT")) {
                            Cell cell = sheet.getRow(i).getCell(j+3);
                            if (packingList.size() != 0) {
                                packingList.get(0).setColumn0(formatter.formatCellValue(cell));
                            }
                        } else if (cellValue.contains("PACKING LIST")) {
                            break breakOut;
                        }

                        materialBreakOut:
                        if (isMatrialRange) {
                            String nextRow = formatter.formatCellValue(sheet.getRow(i + 1).getCell(1));
                            String materialId = "";

                            if (nextRow.startsWith("C'T")) {
                                ctNo = nextRow.split(" : ")[1];
                            }

                            if (cellValue.startsWith("(") && cellValue.endsWith(")")) {
                                materialId = cellValue.replace("(", "").replace(")", "");

                                for (int y = i + 1; y < rowCount; y++) {
                                    ExcelColumn excelColumn = new ExcelColumn(invoiceNo);
                                    for (int z = 0; z < colCount; z++) {
                                        Cell materialCell = sheet.getRow(y).getCell(z);
                                        String materialValue = formatter.formatCellValue(materialCell);

                                        if (materialValue.startsWith("(") && materialValue.endsWith(")")) {
                                            materialId = materialValue.replace("(", "").replace(")", "");
                                        }

                                        if (materialValue.contains("PACKING ACC")) {
                                            isMatrialRange = false;
                                            break materialBreakOut;
                                        } else if (materialValue.contains("TOTAL NET WEIGHT")) {
                                            break breakOut;
                                        } else {
                                            excelColumn.setColumn(z, materialId, materialValue);
                                        }
                                    }
                                    if (!excelColumn.getColumn7().startsWith("(") && !excelColumn.getColumn7().endsWith(")") && (StringUtils.isNotBlank(excelColumn.getColumn7()) || StringUtils.isNotBlank(excelColumn.getColumn16()))) {
                                        materialList.add(excelColumn);
                                    }
                                }
                            }
                        }

                        if (isPackingRange) {
                            String packingId = "";

                            if (cellValue.startsWith("(") && cellValue.endsWith(")")) {
                                packingId = cellValue.replace("(", "").replace(")", "");
                                packingBreak:
                                for (int y = i + 1; y < rowCount; y++) {
                                    ExcelColumn excelColumn = new ExcelColumn(invoiceNo);
                                    for (int z = 0; z < colCount; z++) {
                                        Cell materialCell = sheet.getRow(y).getCell(z);
                                        String materialValue = formatter.formatCellValue(materialCell);

                                        if (materialValue.startsWith("(") && materialValue.endsWith(")")) {
                                            packingId = materialValue.replace("(", "").replace(")", "");
                                        }

                                        excelColumn.setColumn(z, packingId, materialValue);
                                    }

                                    if (StringUtils.isBlank(excelColumn.getColumn6()) && StringUtils.isBlank(excelColumn.getColumn7()) && StringUtils.isBlank(excelColumn.getColumn12()) && StringUtils.isBlank(excelColumn.getColumn16())) {
                                        isPackingRange = false;
                                        break packingBreak;
                                    } else if (StringUtils.isBlank(excelColumn.getColumn7()) && StringUtils.isBlank(excelColumn.getColumn12()) && StringUtils.isBlank(excelColumn.getColumn16())) {
                                        continue;
                                    } else {
                                        if (StringUtils.equals(excelColumn.getColumn7().replace("(", "").replace(")", ""), packingId)) {
                                            continue;
                                        } else {
                                            packingList.add(excelColumn);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            List<ExcelColumn> filterMaterial = new ArrayList<>();
            List<Material> targetMaterialList = new ArrayList<>();
            List<Packing> targetPackingList = new ArrayList<>();

            if (packingList.size() != 0) {
                targetPackingList = makePackingModel(packingList);
            }

            if (materialList.size() != 0) {
                materialList.get(0).setColumn1(ctNo);  //packingList 포장수량을 위한 값
                filterMaterial = filterMaterial(materialList);
                targetMaterialList= makeMaterialModel(filterMaterial);

            }
            resultExcelList = findMatchedPdfAndMakeData(targetMaterialList, targetPackingList);





            /*
                todo
                1. 만들어진 materialList와 packingList를 하나의 모델값으로 변경
                2. 원단정보와 매칭되는 PDF파일을 찾아 매핑되는 정보를 가져와 모델로 만듬
                3. 만들어진 모델 정보로 결과 파일인 excel을 만듬
                4. PDF를 업로드하는 페이지 작업 (이름 중복시 덮어씌움)
                5. PDF검색 기능 작업
                6. 엑셀업로드 작업
                7. 업로드 된 이후 '작업시작' 버튼 눌러 결과 EXCEL 다운로드
             */

            //
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultExcelList;
    }

    private List<ExcelColumn> filterMaterial(List<ExcelColumn> materialList) {
        List<ExcelColumn> filteredMaterials = new ArrayList<>();

        for (ExcelColumn data : materialList) {
            if (StringUtils.isNotBlank(data.getColumn3()) || StringUtils.isNotBlank(data.getColumn4()) ||
                    StringUtils.isNotBlank(data.getColumn6()) || StringUtils.isNotBlank(data.getColumn7()) ||
                    StringUtils.isNotBlank(data.getColumn12()) || StringUtils.isNotBlank(data.getColumn19()) ||
                    StringUtils.isNotBlank(data.getColumn20()) || StringUtils.isNotBlank(data.getColumn21())) {
                filteredMaterials.add(data);
            }
        }
        return filteredMaterials;
    }

    private List<Material> makeMaterialModel(List<ExcelColumn> excelColumnMaterialList) {
        // 2개 라인을 읽어서 1개의 Material으로 작업

        List<Material> materials = new ArrayList<>();

        for (int i = 0; i < excelColumnMaterialList.size(); i++) {
            if (i % 2 == 0) {
                Material material = new Material();

                ExcelColumn data1 = excelColumnMaterialList.get(i);
                ExcelColumn data2 = excelColumnMaterialList.get(i + 1);

                material.setInvoiceNo(data1.getColumn28());
                material.setFabric(data1.getColumn7());
                material.setMaterialId(data1.getColumn27());
                material.setBalesOrRoll(data1.getColumn3(), data1.getColumn4());
                material.setBalesOrRoll(data2.getColumn3(), data2.getColumn4());
                material.setWidth(data2.getColumn7());
                material.setHsCode(data2.getColumn12());
                material.setQuantity(data2.getColumn16());
                material.setUnit(data2.getColumn19());
                material.setUnitPrice(data2.getColumn21());
                material.setTotalPrice(data2.getColumn26());
                if ((i + 2) < excelColumnMaterialList.size()) {
                    ExcelColumn data3 = excelColumnMaterialList.get(i + 2);
                    if (!data1.getColumn28().equals(data3.getColumn28())) {
                        material.setIsLastSameMaterial(true);
                    }
                } else if ((i + 2) == excelColumnMaterialList.size()) {
                    material.setIsLastSameMaterial(true);
                }

                materials.add(material);
            }
        }

        if (materials.size() > 1) {
            if (StringUtils.isNotBlank(excelColumnMaterialList.get(0).getColumn1())) {
                materials.get(0).setCtNo(Integer.parseInt(excelColumnMaterialList.get(0).getColumn1()) + materials.get(0).getRoll() + materials.get(1).getRoll());
            }
        } else {
            if (StringUtils.isNotBlank(excelColumnMaterialList.get(0).getColumn1())) {
                materials.get(0).setCtNo(Integer.parseInt(excelColumnMaterialList.get(0).getColumn1()) + materials.get(0).getRoll());
            }
        }

        return materials;
    }

    private List<Packing> makePackingModel(List<ExcelColumn> excelColumnPackingList) {
        // 2개 라인을 읽어서 1개의 Packing으로 작업
        List<Packing> packings = new ArrayList<>();

        for (int i = 0; i < excelColumnPackingList.size(); i++) {
            if (i % 2 == 0) {
                Packing packing = new Packing();

                ExcelColumn data1 = excelColumnPackingList.get(i);
                ExcelColumn data2 = excelColumnPackingList.get(i + 1);

                packing.setInvoiceNo(data1.getColumn28());
                packing.setPackingId(data1.getColumn27());
                packing.setName(data1.getColumn7());
                packing.setHsCode(data2.getColumn12());
                packing.setQuantity(data2.getColumn16());
                packing.setUnit(data2.getColumn19());
                packing.setUnitPrice(data2.getColumn21());
                packing.setTotalPrice(data2.getColumn26());

                packings.add(packing);
            }
        }
        packings.get(0).setUnitPrice(excelColumnPackingList.get(0).getColumn0());
        return packings;
    }

    private List<ResultExcel> findMatchedPdfAndMakeData(List<Material> materialList, List<Packing> packingList) {
        // 원단 파일을 넘겨받아 PDF에서 해당 매칭 데이터를 찾아 긁어오기

        List<ResultExcel> resultExcels = new ArrayList<>();
        Set<String> materialIds = new HashSet<>();
        for (Material material : materialList) {
            materialIds.add(material.getMaterialId());
        }

        Iterator<String> iterator = materialIds.iterator();

        while (iterator.hasNext()) {
            String materialId = iterator.next();
            String pdfFilePath = daPdfUploadDir + materialId + ".pdf";
            String invoiceNo = "";
            for (Material material : materialList) {
                if (materialId.equals(material.getMaterialId())) {
                    invoiceNo = material.getInvoiceNo();
                }
            }

            String[] lines = readPdf(pdfFilePath);
            log.info("materialId : {}, pdfName : {}, pdfLineCount : {} ", invoiceNo, materialId, lines.length);
            resultExcels.addAll(makeMaterialResultData(materialId, lines, materialList));
            System.out.println(lines.length);
        }

        double materialUnitPriceSum = resultExcels.stream().map(x -> x.getCalculateWeight()).reduce(0.0, (a,b) -> a + b);

        if (packingList.size() != 0) {
            resultExcels.addAll(makePackingResultData(packingList, materialList, materialUnitPriceSum));
        }

        return resultExcels;
    }

    private List<ResultExcel> makePackingResultData(List<Packing> packingList, List<Material> materialList, double materialUnitPriceSum) {
        List<ResultExcel> resultPackingExcelList = new ArrayList<>();
        for (Packing packing : packingList) {
            ResultExcel result = new ResultExcel();

            result.setInvoiceNo(packing.getInvoiceNo());
            result.setProductCode("");
            result.setProductName1("(" + packing.getPackingId() + ")");
            result.setProductName2(packing.getName()); // WIDTH, WEIGHT
            result.setProductName3("");
            result.setFabric("");
            result.setFiberContent1("(" + packing.getPackingId() + ")");
            result.setFiberContent2(packing.getName());
            result.setFiberContent3("");
            result.setCount(packing.getQuantity());
            result.setUnit(packing.getUnit());
            result.setUnitPrice(packing.getUnitPrice());
            result.setTotalPrice(packing.getTotalPrice());
            result.setOrigin("KR");
            result.setPackageUnit("GT");

            resultPackingExcelList.add(result);
        }

        if (materialList.size() != 0) {
            resultPackingExcelList.get(0).setCalculateWeight(packingList.get(0).getUnitPrice() - materialUnitPriceSum);
            resultPackingExcelList.get(0).setPackageCount(materialList.get(0).getCtNo());
        }

        return resultPackingExcelList;
    }

    private List<ResultExcel> makeMaterialResultData(String materialId, String[] lines, List<Material> materialList) {
        List<ResultExcel> resultMaterialExcelList = new ArrayList<>();
        for (Material material : materialList) {
            boolean isTargetBlock = false;
            List<String> targetData = new ArrayList<>();
            if (materialId.equals(material.getMaterialId())) {
                breakThisMaterial:
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].contains(material.getFabric())) {
                        if (lines[i + 4].contains(material.getWidth().split("WIDTH : ")[1]) || lines[i + 5].contains(material.getWidth().split("WIDTH : ")[1])) {
                            isTargetBlock = true;
                        }
                    }

                    if (isTargetBlock) {
                        targetData.add(lines[i]);
                        if (lines[i].contains("YDS") && lines[i].contains("US$")) {
                            if (lines[i].contains(String.valueOf(material.getUnitPrice())) == false) {
                                targetData = new ArrayList<>();
                            } else {
                                isTargetBlock = false;
                                break breakThisMaterial;
                            }
                        }
                    }
                }
            }

            if (targetData.size() != 0) {
                resultMaterialExcelList.add(mappingMaterialAndPdfData(material, targetData));
            }
        }

        return resultMaterialExcelList;
    }

    private ResultExcel mappingMaterialAndPdfData(Material material, List<String> targetData) {
        ResultExcel result = new ResultExcel();

        StringBuilder strBuilder = new StringBuilder();
        String name = targetData.get(0).split(" ")[1];

        strBuilder.append(name + ":" + targetData.get(0).split(" : ")[1]);
        double weight = 0.0;
        boolean isTargetContent = false;
        for (String line : targetData) {
            String appendLine = " " + line;
            if (line.startsWith("WIDTH ")) {
                isTargetContent = true;
            }

            if (line.startsWith("WEIGHT : ")) {
                String tempWeight = line.split("WEIGHT : ")[1].split(" ")[0];
                IntStream stream = tempWeight.chars();
                weight = Double.parseDouble(stream.filter((ch) -> (48 <= ch && ch <= 57) || (ch == 46)).mapToObj(ch -> (char)ch)
                        .map(Object::toString)
                        .collect(Collectors.joining()));
                isTargetContent = true;
            }

            if (line.contains("FIBER CONTENT:")) {
                isTargetContent = true;
            }

            if (line.contains("YDS") && line.contains("US$")) {
                isTargetContent = false;
            }

            if (isTargetContent) {
                strBuilder.append(appendLine.replace("  ", " "));
            }
        }

        result.setInvoiceNo(material.getInvoiceNo());
        result.setProductCode(material.getMaterialId() + material.getUnitPrice());
        result.setProductName1("(" + material.getMaterialId() + ") KNITTED FABRIC");
        if (targetData.get(4).startsWith("WIDTH") && targetData.get(5).startsWith("WEIGHT")) {
            result.setProductName2(targetData.get(4) + " " + targetData.get(5)); // WIDTH, WEIGHT
        } else if (targetData.get(5).startsWith("WIDTH") && targetData.get(6).startsWith("WEIGHT")) {
            result.setProductName2(targetData.get(5) + " " + targetData.get(6)); // WIDTH, WEIGHT
        }
        result.setProductName3(targetData.get(0), material.isLastSameMaterial());
        result.setFabric(material.getFabric());
        result.setFiberContentOrigin(strBuilder.toString());
        result.setCount(material.getQuantity());
        result.setUnit(material.getUnit());
        result.setUnitPrice(material.getUnitPrice());
        result.setTotalPrice(material.getTotalPrice());
        result.setOrigin("KR");
        result.setCalculateWeight((material.getQuantity() * weight) / 1000);
        if (targetData.get(0).contains("BODY")) {
            result.setHsCode(targetData.get(0).split(" BODY")[0].replace(".", ""));
        } else if (targetData.get(0).contains("TRIM")) {
            result.setHsCode(targetData.get(0).split(" TRIM")[0].replace(".", ""));
        }
        result.setPackageUnit("BL");
        result.setPackageCount(material.getBales());
        result.setCompanyName(targetData.get(1) + targetData.get(2) + targetData.get(3));

        return result;
    }

    private static String[] readPdf(String filePath) {
        File file = new File(filePath);
        PDDocument document = null;
        PDFTextStripper pdfStripper = null;
        String pages = "";

        try {
            document = PDDocument.load(file);
            pdfStripper = new PDFTextStripper();
            pages = pdfStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pages.split("\r\n|\r|\n");
    }
}
