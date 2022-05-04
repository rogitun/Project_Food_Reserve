package heading.ground.entity.user;

import heading.ground.entity.book.Book;
import heading.ground.entity.post.Comment;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.StudentForm;
import heading.ground.forms.user.UserEditForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends BaseUser {

    private String email;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Book> books = new ArrayList<>();

    public Student(BaseSignUp std) {
        this.loginId = std.getLoginId();
        this.password = std.getPassword();
        this.name = std.getName();
        this.email = std.getEmail();
        this.phoneNumber = std.getPhoneNumber();
        this.role = MyRole.STUDENT;
        //TODO 추후 이메일 인증 후에 풀어주도록 로직 설정
        this.non_locked = true;
    }

    public void update(UserEditForm form) {
        name = form.getName();
        email = form.getEmail();
        phoneNumber = form.getPhoneNumber();
    }
}
