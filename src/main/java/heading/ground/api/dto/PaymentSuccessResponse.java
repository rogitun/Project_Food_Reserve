package heading.ground.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessResponse {

    private int count;
    private HttpStatus httpStatus;
    private Integer code;
    private PaymentSuccessDetails paymentSuccessDetails;

}
