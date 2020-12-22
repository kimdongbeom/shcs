package sinhan.custom.shcs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultExcel {
    private String invoiceNo;
    private String productCode;
    private String productName1;
    private String productName2;
    private String productName3;
    private String fabric;
    private String fiberContentOrigin;
    private String fiberContent1;
    private String fiberContent2;
    private String fiberContent3;
    private String fiberContent4;
    private String fiberContent5;
    private String fiberContent6;
    private String fiberContent7;
    private String fiberContent8;
    private String fiberContent9;
    private double count; // 수량
    private String unit; //수량단위
    private double unitPrice; //단가
    private double totalPrice; //금액
    private String origin; //원산지
    private double calculateWeight; //순중량
    private String hsCode;
    private String packageUnit; //포장단위
    private int packageCount; //포장수량
    private String companyName;

    public void setUnit(String unit) {
        if (unit.equals("YDS")) {
            this.unit = "YD";
        } else if (unit.equals("CONE")) {
            this.unit = "CJ";
        } else if (unit.equals("PCS")) {
            this.unit = "PC";
        } else if (unit.equals("RILL")) {
            this.unit = "RL";
        } else if (unit.equals("GROSS")) {
            this.unit = "GRO";
        } else {
            this.unit = unit;
        }
    }

    public void setProductName3(String hsCode, boolean lastSameMaterial) {
        String hsCodeMiddleValue = hsCode.split("\\.")[1]; //6006.22.0000  => 22,32,42이면 DYED, 24,34,44 이면 PRINT
        String value1 = "";
        String value2 = "";
        if (hsCodeMiddleValue.endsWith("2")) {
            value1 = "DYED ";
        } else if (hsCodeMiddleValue.endsWith("4")) {
            value1 = "PRINT ";
        }

        if (lastSameMaterial) {
            value2 = "ATTACHED ITEM";
        }
        this.productName3 = value1 + value2;
    }

    public void setFiberContentOrigin(String contents) {
        String[] splitLine;
        int contentsLength = contents.length();
        if (contentsLength < 400) {
            this.fiberContent1 = contents.substring(0, 49);
            this.fiberContent2 = contents.substring(49, 99);
            this.fiberContent3 = contents.substring(99, contentsLength);
        } else if ((400 <= contents.length()) && (contents.length() < 750)) {
            this.fiberContent1 = "(CONTINUE - 01/02)";
            this.fiberContent2 = contents.substring(0, 49);
            this.fiberContent3 = contents.substring(49, 349);
            this.fiberContent4 = "(CONTINUE - 02/02)";
            if (contents.length() < 400) {
                this.fiberContent5 = contents.substring(349, contentsLength);
            } else {
                this.fiberContent5 = contents.substring(349, 399);
                this.fiberContent6 = contents.substring(399, contentsLength);
            }

        } else if ((750 <= contents.length()) && (contents.length() < 1100)) {
            this.fiberContent1 = "(CONTINUE - 01/03)";
            this.fiberContent2 = contents.substring(0, 49);
            this.fiberContent3 = contents.substring(49, 349);
            this.fiberContent4 = "(CONTINUE - 02/03)";
            this.fiberContent5 = contents.substring(349, 399);
            this.fiberContent6 = contents.substring(399, 749);
            this.fiberContent7 = "(CONTINUE - 03/03)";
            if (contents.length() < 800) {
                this.fiberContent8 = contents.substring(749, contentsLength);
            } else {
                this.fiberContent8 = contents.substring(749, 799);
                this.fiberContent9 = contents.substring(799, contentsLength);
            }
        }
    }
}
