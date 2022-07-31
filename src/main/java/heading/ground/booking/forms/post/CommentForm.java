package heading.ground.booking.forms.post;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Data
public class CommentForm {

    @NotBlank
    @Length(min = 5, max = 99)
    private String desc;

    @NotBlank
    private String star;

    private boolean isStudent;
}
