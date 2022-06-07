package heading.ground.forms.user;

import heading.ground.entity.user.Seller;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Slf4j
@NoArgsConstructor
public class SellerEditForm {

    private Long id;

    private String name; //가게 이름

    private String phoneNumber; //가게 전화번호

    private String desc;

    private String email;

    private String sellerId;

    private String zipCode;
    private String doro;
    private String doroSpec;

    private String category;

    public SellerEditForm(Seller seller) {
        this.id = seller.getId();
        this.name = seller.getName();
        this.phoneNumber = seller.getPhoneNumber();
        this.desc = seller.getDesc();
        this.sellerId = seller.getCompanyId();
        this.email = seller.getEmail();
        this.doro = seller.getAddress().getDoro();
        this.doroSpec = seller.getAddress().getDoro_spec();
        this.zipCode = seller.getAddress().getZipCode();

        if(seller.getCategory()!=null){
            this.category = seller.getCategory().getName();
        }
    }
}
