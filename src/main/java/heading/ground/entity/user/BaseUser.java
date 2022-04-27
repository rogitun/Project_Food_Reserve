package heading.ground.entity.user;

import heading.ground.entity.Base;
import heading.ground.forms.user.SellerEditForm;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class BaseUser extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected Long id;

    protected String loginId;

    protected String password;

    protected String name; // 이름(가게이름, 학생 별명)

   protected String phoneNumber;

   @Enumerated(EnumType.STRING)
   protected MyRole role;

    public void update(SellerEditForm form){
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
    }

}
