package heading.ground.booking.dto.bookdtos;

import heading.ground.user.entity.Seller;
import lombok.Data;

@Data
public class BookSellerDto {

    private Long id;

    private String name;

    private String photo;

    public BookSellerDto(Seller seller) {
        id = seller.getId();
        name = seller.getName();
        if(seller.getImageFile()!=null)
            photo = seller.getImageFile().getStoreName();
    }
}
