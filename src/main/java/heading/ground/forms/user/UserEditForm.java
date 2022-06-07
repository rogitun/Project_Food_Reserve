package heading.ground.forms.user;

import heading.ground.entity.user.Student;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserEditForm {

    private Long id;

    @NotBlank
    @Length(max = 16)
    String name;

    @NotBlank
    @Pattern(regexp = "^\\d{2,4}-\\d{3,4}-\\d{4}$",message = "00/000-0000-0000 형태로 입력해주세요")
    String phoneNumber;

    @NotBlank
    @Email
    String email;

    public UserEditForm(Student std) {
        id = std.getId();
        this.name = std.getName();
        this.phoneNumber = std.getPhoneNumber();
        this.email = std.getEmail();
    }
}
