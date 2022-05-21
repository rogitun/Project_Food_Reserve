package heading.ground.dto.user;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.entity.ImageFile;
import heading.ground.entity.book.Book;
import heading.ground.entity.user.Address;
import heading.ground.entity.user.Seller;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SellerDto {

    private Long id;
    private String name; //가게 이름
    private int seats; //좌석 수

    private String phoneNumber;

    private Address address;

    private String sellerId;

    private String email;

    private String desc;

    private String photo;

    private String category;

    public SellerDto(Seller seller) {
        this.id = seller.getId();
        this.name = seller.getName();
        this.phoneNumber = seller.getPhoneNumber();
       // this.address = seller.getAddress();
        this.sellerId = seller.getCompanyId();
        this.desc = seller.getDesc();
        photoCheck(seller.getImageFile());
        if(seller.getCategory()!=null)
            this.category = seller.getCategory().getName();
    }

    public void photoCheck(ImageFile temp){
        if(temp==null){
            return;
        }
        this.photo = temp.getStoreName();
    }

}
