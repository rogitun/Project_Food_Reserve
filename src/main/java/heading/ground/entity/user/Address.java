package heading.ground.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Data
public class Address {

    @Column(length = 10)
    private String doro; //도로명 주소 1

    @Column(length = 10)
    private String doro_spec; //상세 주소

    @Column(length = 10)
    private int zipCode; //지번
}
