package heading.ground.entity.user;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Address {
    
    private String doro; //도로명 주소 1
    private String spec; //상세 주소
    private String zipCode; //지번
    
}
