package heading.ground.dto.util;

import heading.ground.entity.post.Menu;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartMenuDto {

//    장바구니 DTD에서 필요ㅕ한거
//    맨 위에 가게이름
//    메뉴 => ID, 이름, 가격
    private Long id;
    private String name;
    private int price;
    private String sellerName;
    private boolean isOut;
}
