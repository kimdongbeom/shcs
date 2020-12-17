package sinhan.custom.shcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    private String invoiceNo;
    private String materialId; //pdf를 찾는 수단이 된다.
    private int bales;
    private int roll;
    private String fabric;
    private String width;
    private String hsCode;
    private double quantity;
    private String unit; //ex : YDS, PCS, RILL, CONE
    private double unitPrice;  // 단가
    private double totalPrice;
    private boolean isLastSameMaterial = false;
    private int ctNo; //packinglist의 포장수량을 구하기 위함 (ctNo + roll)

    public void setIsLastSameMaterial(boolean flag) {
        this.isLastSameMaterial = flag;
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

    public void setHsCode(String hsCode) {
        String code = hsCode.split(" : ")[1];
        this.hsCode = code;
    }

    public void setBalesOrRoll(String count, String name) {
        if (StringUtils.equals("BALES", name)) {
            this.bales = Integer.parseInt(count);
        } else if (StringUtils.equals("ROLL", name)) {
            this.roll = Integer.parseInt(count);
        }
    }
}
