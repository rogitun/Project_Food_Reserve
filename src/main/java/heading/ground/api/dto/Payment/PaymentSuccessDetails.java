package heading.ground.api.dto.Payment;

import lombok.Data;

@Data
public class PaymentSuccessDetails {
    private String merchant_uid;
    private String buyer_name;
    private String buyer_email;
    private String card_name;
    private int amount;
    private String status;
    private boolean success;
}
