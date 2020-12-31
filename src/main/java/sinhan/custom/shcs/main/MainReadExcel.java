package sinhan.custom.shcs.main;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import sinhan.custom.shcs.model.ExcelColumn;
import sinhan.custom.shcs.model.Material;
import sinhan.custom.shcs.model.Packing;
import sinhan.custom.shcs.model.ResultExcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainReadExcel {

    private static final String pdfFileFolder = "/Users/nhnent/Desktop/shcs/work2/pdf/";

    public static void main(String[] args) {
        // 웹상에서 업로드 되어 MultipartFile인 경우 바로 InputStream으로 변경하여 사용.
        // InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        //
        // String filePath = "D:\\student.xlsx"; // xlsx 형식
        String filePath = "/Users/nhnent/Desktop/shcs/work2/thiswork/HSVSS-201126.4-1.xls"; // xls 형식
         // 엑셀 로드
        try {
            InputStream inputStream = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowItr = sheet.iterator();
            boolean isMatrialRange = false;
            boolean isPackingRange = false;

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(3).getPhysicalNumberOfCells();

            List<ExcelColumn> materialList = new ArrayList<>();
            List<ExcelColumn> packingList = new ArrayList<>();

            breakOut:
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {

                    Cell currentCell = sheet.getRow(i).getCell(j);

                    DataFormatter formatter = new DataFormatter();

                    String cellValue = formatter.formatCellValue(currentCell);
                    if (StringUtils.isNotBlank(cellValue)) {
//                        System.out.println(cellValue);
                        if (cellValue.contains("MATERIAL FOR KNITTED")) {
                            System.out.println(cellValue);
                            isMatrialRange = true;
                        } else if (cellValue.contains("PACKING ACC")) {
                            isMatrialRange = false;
                            isPackingRange = true;
                        } else if (cellValue.contains("PACKING LIST")) {
                            break breakOut;
                        }

                        materialBreakOut:
                        if (isMatrialRange) {
                            String materialId = "";
                            if (cellValue.startsWith("(") && cellValue.endsWith(")")) {
                                materialId = cellValue.replace("(", "").replace(")", "");

                                for (int y = i + 1; y < rowCount; y++) {
                                    ExcelColumn excelColumn = new ExcelColumn();
                                    for (int z = 0; z < colCount; z++) {
                                        Cell materialCell = sheet.getRow(y).getCell(z);
                                        String materialValue = formatter.formatCellValue(materialCell);

                                        if (materialValue.startsWith("(") && materialValue.endsWith(")")) {
                                            materialId = cellValue.replace("(", "").replace(")", "");
                                        }

                                        if (materialValue.contains("PACKING ACC")) {
                                            isMatrialRange = false;
                                            break materialBreakOut;
                                        } else {
                                            excelColumn.setColumn(z, materialId, materialValue);
                                        }
                                    }
                                    materialList.add(excelColumn);
                                }
                            }
                        }

                        if (isPackingRange) {
                            String packingId = "";

                            if (cellValue.startsWith("(") && cellValue.endsWith(")")) {
                                packingId = cellValue.replace("(", "").replace(")", "");
                                for (int y = i + 1; y < rowCount; y++) {
                                    ExcelColumn excelColumn = new ExcelColumn();
                                    for (int z = 0; z < colCount; z++) {
                                        Cell materialCell = sheet.getRow(y).getCell(z);
                                        String materialValue = formatter.formatCellValue(materialCell);

                                        if (materialValue.startsWith("(") && materialValue.endsWith(")")) {
                                            packingId = materialValue.replace("(", "").replace(")", "");
                                        }

                                        excelColumn.setColumn(z, packingId, materialValue);
                                    }

                                    if (StringUtils.isBlank(excelColumn.getColumn6()) && StringUtils.isBlank(excelColumn.getColumn7()) && StringUtils.isBlank(excelColumn.getColumn12())) {
                                        break breakOut;
                                    } else if (StringUtils.isBlank(excelColumn.getColumn7()) && StringUtils.isBlank(excelColumn.getColumn12())) {
                                        continue;
                                    } else {
                                        if (StringUtils.equals(excelColumn.getColumn7().replace("(", "").replace(")", ""), packingId)) {
                                            continue;
                                        } else {
                                            packingList.add(excelColumn);
                                        }
                                    }
//
//                                    if (cellValue.contains("TOTAL MEASUREMENT")) {
//                                        break breakOut;
//                                    }
                                }
                            }

                        }
                    }
                }
            }


            List<Material> targetMaterialList = makeMaterialModel(materialList);
            List<Packing> targetPackingList = makePackingModel(packingList);

            List<ResultExcel> resultExcelList = findMatchedPdfAndMakeData(targetMaterialList);
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
    }

    private static List<Material> makeMaterialModel(List<ExcelColumn> excelColumnMaterialList) {
        // 2개 라인을 읽어서 1개의 Material으로 작업

        List<Material> materials = new ArrayList<>();

        for (int i = 0; i < excelColumnMaterialList.size(); i++) {
            if (i % 2 == 0) {
                Material material = new Material();

                ExcelColumn data1 = excelColumnMaterialList.get(i);
                ExcelColumn data2 = excelColumnMaterialList.get(i + 1);

                material.setFabric(data1.getColumn7().toUpperCase());
                material.setMaterialId(data1.getColumn27());
                material.setBalesOrRoll(data1.getColumn3(), data1.getColumn4());
                material.setBalesOrRoll(data2.getColumn3(), data2.getColumn4());
                material.setWidth(data2.getColumn7());
                material.setHsCode(data2.getColumn12());
                material.setQuantity(data2.getColumn16());
                material.setUnit(data2.getColumn19());
                material.setUnitPrice(data2.getColumn21());
                material.setTotalPrice(data2.getColumn26());
                if (i == excelColumnMaterialList.size() - 1) {
                    material.setIsLastSameMaterial(true);
                }

                materials.add(material);
            }
        }

        return materials;
    }

    private static List<Packing> makePackingModel(List<ExcelColumn> excelColumnPackingList) {
        // 2개 라인을 읽어서 1개의 Packing으로 작업
        System.out.println(excelColumnPackingList.size());

        List<Packing> packings = new ArrayList<>();

        for (int i = 0; i < excelColumnPackingList.size(); i++) {
            if (i % 2 == 0) {
                Packing packing = new Packing();

                ExcelColumn data1 = excelColumnPackingList.get(i);
                ExcelColumn data2 = excelColumnPackingList.get(i + 1);

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
        return packings;
    }

    private static List<ResultExcel> findMatchedPdfAndMakeData(List<Material> materialList) {
        // 원단 파일을 넘겨받아 PDF에서 해당 매칭 데이터를 찾아 긁어오기

        Set<String> materialIds = new HashSet<>();
        for (Material material : materialList) {
            materialIds.add(material.getMaterialId());
        }

        Iterator<String> iterator = materialIds.iterator();

        while (iterator.hasNext()) {
            String materialId = iterator.next();
            String pdfFilePath = pdfFileFolder + materialId;

            String[] lines = readPdf(pdfFilePath);
            makeReultData(materialId, lines, materialList);
            System.out.println(lines.length);
        }
        /**
         * find pdf
         * pdf에서 매칭되는 데이터 가져오기
         * 데이터 가져와서 엑셀로 만들기 (자재, 부자재)
         *
         */
        //
        return null;
    }

    private static void makeReultData(String materialId, String[] lines, List<Material> materialList) {
        List<String> targetData = new ArrayList<>();
        List<ResultExcel> resultDataList = new ArrayList<>();
        for (Material material : materialList) {
            boolean isTargetBlock = false;
            if (materialId.equals(material.getMaterialId())) {
                breakThisMaterial:
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].contains(material.getFabric())) {
                        if (lines[i + 4].contains(material.getWidth().split("WIDTH : ")[1])) {
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

            // handle data
            System.out.println(targetData.size());
            resultDataList.add(convertResultDataList(material, targetData));
            /**
             * todo
             * resultDataList와 packingList가지고 최종 엑셀 만들기
             */
        }
    }

    private static ResultExcel convertResultDataList(Material material, List<String> targetData) {
        ResultExcel result = new ResultExcel();

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("BODY" + targetData.get(0).split("BODY  ")[1]);
        double weight = 0.0;
        boolean isTargetContent = false;
        for (String line : targetData) {
            String appendLine = "," + line;
            if (line.startsWith("WIDTH ")) {
                strBuilder.append(appendLine);
            }

            if (line.startsWith("WEIGHT : ")) {
                weight = Double.parseDouble(line.split("WEIGHT : ")[1].split(" ")[0]);
                strBuilder.append(appendLine);
            }

            if (line.contains("FIBER CONTENT:")) {
                isTargetContent = true;
            }

            if (line.contains("YDS") && line.contains("US$")) {
                isTargetContent = false;
            }

            if (isTargetContent) {
                strBuilder.append(appendLine);
            }
        }

        result.setInvoiceNo("");
        result.setProductCode(material.getMaterialId() + material.getUnitPrice());
        result.setProductName1(material.getMaterialId(), material.getHsCode());
        result.setProductName2(targetData.get(4) + " " + targetData.get(5)); // WIDTH, WEIGHT
        result.setProductName3(targetData.get(0), material.isLastSameMaterial());
        result.setFabric(material.getFabric());
        result.setFiberContentOrigin(strBuilder.toString());
        result.setCount(material.getQuantity());
        result.setUnit(material.getUnit());
        result.setUnitPrice(material.getUnitPrice());
        result.setTotalPrice(material.getTotalPrice());
        result.setOrigin("KR");
        result.setCalculateWeight((material.getQuantity() * weight) / 1000);
        result.setHsCode(targetData.get(0).split(" BODY")[0].replace(".", ""));
        result.setPackageUnit("BL");
        result.setPackageCount(material.getBales());
//        result.setCompanyName(); //todo 추후 셋팅

        return result;
    }

    private static String[] readPdf(String filePath) {
        String filePath1 = filePath + ".pdf";
        String filePath2 = filePath + ".PDF";

        File file = new File(filePath1);
        if (file == null) {
            file = new File(filePath2);
        }

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
