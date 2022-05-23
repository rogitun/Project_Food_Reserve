package heading.ground.api.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private Integer count;
    private PaymentDetails paymentDetails;
}
