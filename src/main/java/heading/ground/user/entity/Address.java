package heading.ground.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
@Data
@NoArgsConstructor
public class Address {

    @Column(length = 25)
    private String doro; //도로명 주소 1

    @Column(length = 25)
    private String doro_spec; //상세 주소

    @Column(length = 10)
    private String zipCode; //지번


    public Address(String doro, String doro_spec, String zipCode) {
        this.doro = doro;
        this.doro_spec = doro_spec;
        this.zipCode = zipCode;
    }

    public void setAddress(String d, String ds, String z){
        this.doro = d;
        this.doro_spec = ds;
        this.zipCode = z;
    }

}
