package sinhan.custom.shcs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Packing {
    private String packingId;
    private String name;
    private String hsCode;
    private String unit; //ex : EA, PCS
    private double quantity; //수량
    private double unitPrice; //단가
    private double totalPrice; //금액

    public void setHsCode(String hsCode) {
        String code = hsCode.split(" : ")[1];
        this.hsCode = code;
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
}