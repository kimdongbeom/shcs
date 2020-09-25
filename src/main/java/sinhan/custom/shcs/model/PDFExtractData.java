package sinhan.custom.shcs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PDFExtractData {
    private int invoiceOrder;
    private int invoiceNo;
    private String materialNo;
    private Double quantity;
}
