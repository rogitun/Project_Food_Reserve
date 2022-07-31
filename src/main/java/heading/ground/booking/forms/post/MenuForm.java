package heading.ground.booking.forms.post;

import heading.ground.booking.entity.post.Menu;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class MenuForm {

    @NotBlank
    private String name; //음식 이름

    @Range(min = 100,max = 999999)
    private int price; //음식 가격

    @NotBlank
    private String desc; //음식 설명

    private String sources;//음식에 들어가는 재료, 굳이 엔티로 뽑을 필요 없음

    private String image_name;
    private MultipartFile image;

    public MenuForm() {
    }

    public MenuForm(Menu menu){
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.desc = menu.getDesc();
        this.sources = menu.getSources();
        if(menu.getImage()!=null)
            this.image_name = menu.getImage().getOriginName();
    }

    //TODO 테스트용 -> 배포시 삭제
    public MenuForm(String name, int price, String desc, String sources) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.sources = sources;
    }

    public Menu toEntity(){
        return new Menu(this);
    }

}
