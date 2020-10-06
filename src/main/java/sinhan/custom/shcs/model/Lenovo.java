package sinhan.custom.shcs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lenovo {
    private String htsCode;
    private String productIdentification;
    private String ctryOfOrigin;
    private String productDescription;
    private String quantity;
    private String uom;
    private String unitPrice;
    private String amount;
    private String invoiceNo;
    private String invoiceTotalAmount;
    private String totalGrossWeight;

    public Lenovo(String value) {
        String[] splitValue = value.split("\\^");
        this.htsCode = splitValue[0];
        this.productIdentification = splitValue[1];
        this.ctryOfOrigin = splitValue[2];
        this.productDescription = splitValue[3];
        this.quantity = splitValue[4];
        this.uom = splitValue[5];
        this.unitPrice = splitValue[6];
        this.amount = splitValue[7];
        this.invoiceNo = splitValue[8];
        this.invoiceTotalAmount = splitValue[9];
        this.totalGrossWeight = splitValue[10];
    }


    public void splitLineDataDN(String line) {
        String[] splitDatas = line.split(" ");
        String unitProductDescription = "";
        int splitDatasLength = splitDatas.length;
        setAmount(splitDatas[splitDatasLength - 1]);
        setUnitPrice(splitDatas[splitDatasLength - 2]);
        setQuantity(splitDatas[splitDatasLength - 3]);
        setUom(splitDatas[splitDatasLength - 4]);
        if (splitDatas[splitDatasLength - 5].length() == 10) {
            setHtsCode(splitDatas[splitDatasLength - 5]);
            setCtryOfOrigin(splitDatas[splitDatasLength - 6]);
            setProductIdentification(splitDatas[splitDatasLength - 7]);
            for(int i=0; i < splitDatasLength - 7; i++) {
                unitProductDescription = unitProductDescription + splitDatas[i] + " ";
            }
        } else if (splitDatas[splitDatasLength - 5].length() == 2) {
            setCtryOfOrigin(splitDatas[splitDatasLength - 5]);
            setProductIdentification(splitDatas[splitDatasLength - 6]);
            for(int i=0; i < splitDatasLength - 6; i++) {
                unitProductDescription = unitProductDescription + splitDatas[i] + " ";
            }
        }
        setProductDescription(unitProductDescription.trim());
    }

    public void splitLineDataDEL(String line) {
        String[] splitDatas = line.split(" ");
        String unitProductDescription = "";
        int splitDatasLength = splitDatas.length;
        setAmount(splitDatas[splitDatasLength - 1]);
        setUnitPrice(splitDatas[splitDatasLength - 2]);
        setQuantity(splitDatas[splitDatasLength - 3]);
        setUom(splitDatas[splitDatasLength - 4]);
        setHtsCode(splitDatas[splitDatasLength - 5]);
        setCtryOfOrigin(splitDatas[splitDatasLength - 6]);
        setProductIdentification(splitDatas[splitDatasLength - 7]);
    }
}
