package heading.ground.dto.user;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.entity.ImageFile;
import heading.ground.entity.book.Book;
import heading.ground.entity.user.Address;
import heading.ground.entity.user.Seller;
import heading.ground.entity.util.Category;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
