package sinhan.custom.shcs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class Packing {
    private String invoiceNo;
    private String packingId;
    private String name;
    private String hsCode;
    private String unit; //ex : EA, PCS
    private double quantity; //수량
    private double unitPrice; //단가
    private double totalPrice; //금액
    private double totalNetWeight;

    public void setHsCode(String hsCode) {
        if (StringUtils.isNotBlank(hsCode)) {
            hsCode = hsCode.split(" : ")[1];
        }
        this.hsCode = hsCode;
    }

    public void setUnitPrice(String value) {
        this.unitPrice = Double.parseDouble(value.replace(",", ""));
    }

    public void setTotalPrice(String value) {
        this.totalPrice = Double.parseDouble(value.replace(",", ""));
    }

    public void setQuantity(String value) {
        this.quantity = Double.parseDouble(value.replace(",", ""));
    }

    public void setTotalNetWeight(String value) {
        this.totalNetWeight = Double.parseDouble(value.replace(",", ""));
    }
}