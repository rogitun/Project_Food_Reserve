package heading.ground.booking.dto.postDtos;

import heading.ground.booking.entity.post.Menu;
import lombok.Data;

@Data
public class MenuDto {

    private Long id;
    private String name; //음식 이름
    private int price; //음식 가격

    private String desc; //음식 설명

    private String sources;//음식에 들어가는 재료, 굳이 엔티로 뽑을 필요 없음

    //메뉴가 소속된 가게
    private String seller;

    private int commentNumber;
    private int star;

    private boolean isBest;

    private boolean isOut;

    //storeName으로 저장
    private String image;

    public MenuDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.desc = menu.getDesc();
        this.sources = menu.getSources();
        this.seller = menu.getSeller().getName();
        this.star = menu.getStar();
        this.isBest = menu.isBest();
        this.isOut = menu.isOutOfStock();
        this.commentNumber = menu.getCommentNumber();
        if(menu.getImage()!=null){
            this.image = menu.getImage().getStoreName();
        }

    }
}
