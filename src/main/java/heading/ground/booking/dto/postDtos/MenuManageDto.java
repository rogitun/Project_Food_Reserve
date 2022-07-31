package heading.ground.booking.dto.postDtos;

import heading.ground.booking.entity.post.Menu;
import lombok.Data;


@Data
public class MenuManageDto {

    private Long id;

    private String name; //음식 이름

    private int price; //음식 가격

    private String desc; //음식 설명

    private int star;

    private boolean outOfStock;

    private boolean isBest;

    public MenuManageDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.desc = menu.getDesc();
        this.star = menu.getStar();
        this.outOfStock = menu.isOutOfStock();
        this.isBest = menu.isBest();
    }

    public String getStatus(){
        String stock = outOfStock?"품절":"품절아님";
        String best = isBest?"대표 상품":"대표 상품 아님";
        return stock +" & " +best;
    }
}
