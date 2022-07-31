package heading.ground.user.entity;

import heading.ground.booking.entity.book.Book;
import heading.ground.booking.entity.post.Comment;
import heading.ground.user.forms.UserEditForm;
import lombok.Builder;
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

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    @Builder
    public Student(String id, String name, String email,String pwd, String phone){
        this.loginId = id;
        this.name = name;
        this.email = email;
        this.password = pwd;
        this.phoneNumber = phone;
        this.role = MyRole.STUDENT;
        this.non_locked = true;
    }

    public void update(UserEditForm form) {
        name = form.getName();
        email = form.getEmail();
        phoneNumber = form.getPhoneNumber();
    }

}
