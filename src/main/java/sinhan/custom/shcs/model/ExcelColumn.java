package sinhan.custom.shcs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExcelColumn {
    private String column0;  // packingList의 첫 객체에만 Total Net Weight값이 들어간다.
    private String column1;  // material의 경우에는 C'T NO 값으로 사용,
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private String column10;
    private String column11;
    private String column12;
    private String column13;
    private String column14;
    private String column15;
    private String column16;
    private String column17;
    private String column18;
    private String column19;
    private String column20;
    private String column21;
    private String column22;
    private String column23;
    private String column24;
    private String column25;
    private String column26;
    private String column27; //pdf name
    private String column28; //invoiceNo

    public ExcelColumn(String invoiceNo) {
        this.column28 = invoiceNo;
    }

    public void setColumn1(String ctNo) {
        if (ctNo.contains("-")) {
            this.column1 = ctNo.split("-")[1];
        } else {
            this.column1 = ctNo;
        }
    }

    public void setColumn(int columnNumber, String id, String columnValue) {
        columnNumber = columnNumber + 1;
        this.column27 = id;
        switch(columnNumber) {
            case 0:
                this.column1 = columnValue;
                break;
            case 1:
                this.column2 = columnValue;
                break;
            case 2:
                this.column3 = columnValue;
                break;
            case 3:
                this.column4 = columnValue;
                break;
            case 4:
                this.column5 = columnValue;
                break;
            case 5:
                this.column6 = columnValue;
                break;
            case 6:
                this.column7 = columnValue;
                break;
            case 7:
                this.column8 = columnValue;
                break;
            case 8:
                this.column9 = columnValue;
                break;
            case 9:
                this.column10 = columnValue;
                break;
            case 10:
                this.column11 = columnValue;
                break;
            case 11:
                this.column12 = columnValue;
                break;
            case 12:
                this.column13 = columnValue;
                break;
            case 13:
                this.column14 = columnValue;
                break;
            case 14:
                this.column15 = columnValue;
                break;
            case 15:
                this.column16 = columnValue;
                break;
            case 16:
                this.column17 = columnValue;
                break;
            case 17:
                this.column18 = columnValue;
                break;
            case 18:
                this.column19 = columnValue;
                break;
            case 19:
                this.column20 = columnValue;
                break;
            case 20:
                this.column21 = columnValue;
                break;
            case 21:
                this.column22 = columnValue;
                break;
            case 22:
                this.column23 = columnValue;
                break;
            case 23:
                this.column24 = columnValue;
                break;
            case 24:
                this.column25 = columnValue;
                break;
            case 25:
                this.column26 = columnValue;
                break;
        }
    }
}
