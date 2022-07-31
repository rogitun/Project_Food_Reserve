package heading.ground.booking.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartListSuccessResponse {

    private HttpStatus httpStatus;
    private Integer code;
    private String message;
}
