package heading.ground.dto.user;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.entity.ImageFile;
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

    private List<MenuDto> menus = new ArrayList<>();

    private List<BookDto> books = new ArrayList<>();

    private String photo;

    public SellerDto(Seller seller) {
        this.id = seller.getId();
        this.name = seller.getName();
        this.phoneNumber = seller.getPhoneNumber();
       // this.address = seller.getAddress();
        this.sellerId = seller.getCompanyId();
        this.desc = seller.getDesc();
        photoCheck(seller.getImageFile());
        log.info("query tRack = {} ",seller.getMenus().size());
        if(!seller.getMenus().isEmpty()) {
            menus = seller.getMenus()
                    .stream()
                    .map(m -> new MenuDto(m))
                    .collect(Collectors.toList());
        }
        if(!seller.getBooks().isEmpty()){
            books = seller.getBooks().stream().map(b-> new BookDto(b)).collect(Collectors.toList());
        }
    }

    public void setSellerWithMenus(Seller seller){
        this.id = seller.getId();
        this.name = seller.getName();
        this.phoneNumber = seller.getPhoneNumber();
       // this.address = seller.getAddress();
        this.sellerId = seller.getCompanyId();
        this.desc = seller.getDesc();
        photoCheck(seller.getImageFile());
        log.info("query tRack = {} ",seller.getMenus().size());
        if(!seller.getMenus().isEmpty()) {
            menus = seller.getMenus()
                    .stream()
                    .map(m -> new MenuDto(m))
                    .collect(Collectors.toList());
        }
    }

    public void photoCheck(ImageFile temp){
        if(temp==null){
            return;
        }
        this.photo = temp.getStoreName();
    }

}
