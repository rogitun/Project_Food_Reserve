package heading.ground.booking.dto.postDtos;

import heading.ground.booking.entity.post.Menu;
import lombok.Data;

@Data
public class MenuListDto {

    private String name;

    private int price;

    private boolean isOut;

    private boolean isBest;

    public MenuListDto(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public MenuListDto(Menu menu) {
        name = menu.getName();
        price = menu.getPrice();
        isOut = menu.isOutOfStock();
        isBest = menu.isBest();
    }
}
