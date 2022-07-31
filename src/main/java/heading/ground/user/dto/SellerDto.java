package heading.ground.user.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Builder
public class SellerDto {

    private Long id;
    private String name; //가게 이름
    private int seats; //좌석 수

    private String phoneNumber;

    private String doro;
    private String doro_spec;


    private String sellerId;

    private String email;

    private String desc;

    private String photo;

    private String category;
}
