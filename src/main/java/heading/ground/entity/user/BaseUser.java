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
import java.util.Date;

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

    protected String email;

    @Enumerated(EnumType.STRING)
   protected MyRole role;

   @Column(columnDefinition = "boolean default true")
   protected boolean non_locked;

   @Column(columnDefinition = "tinyint default 0")
   protected int failed_attempt;

   protected Date lock_time;

   protected String uuid;

    public void update(SellerEditForm form){
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
    }

    public void setNon_locked(boolean non_locked) {
        this.non_locked = non_locked;
    }

    public void addFailed_attempt(boolean flag) {
        failed_attempt = flag?(failed_attempt+1):0;
        System.out.println("fail attempt :" + failed_attempt);
    }

    public void setLock_time(Date lock_time) {
        this.lock_time = lock_time;
    }

    public void setUUID(String uuid){
        this.uuid = uuid;
    }

    public void changePassword(String encode) {
        this.failed_attempt=0;
        this.non_locked = true;
        this.uuid = null;
        this.password = encode;
    }
}
