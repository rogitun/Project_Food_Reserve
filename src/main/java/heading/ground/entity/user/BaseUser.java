package heading.ground.entity.user;

import heading.ground.entity.Base;
import heading.ground.forms.user.SellerEditForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
@NoArgsConstructor
public class BaseUser extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected Long id;

    @Size(max = 10)
    @NotNull
    protected String loginId;

    @Size(max = 100)
    @NotNull
    protected String password;

    @Size(max = 16)
    @NotNull
    protected String name; // 이름(가게이름, 학생 별명)

    @Size(max = 14)
    @NotNull
    protected String phoneNumber;

    @Size(max = 40)
    @NotNull
    protected String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    protected MyRole role;

   @Column(columnDefinition = "boolean default true")
   protected boolean non_locked;

   @Column(columnDefinition = "tinyint default 0")
   protected int failed_attempt;

   protected Date lock_time;

   protected String uuid;

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

    //TODO 임시
    public void setRole(){
        this.role = MyRole.ADMIN;
    }
}
