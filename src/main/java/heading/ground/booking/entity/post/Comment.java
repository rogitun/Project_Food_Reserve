package heading.ground.booking.entity.post;

import heading.ground.common.Base;
import heading.ground.user.entity.Student;
import heading.ground.booking.forms.post.CommentForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

//TODO 댓글 고려사항
/**
 * 1. 댓글은 메뉴와 사용자의 연관관계를 가진다.
 * 2. 댓글과 메뉴는 M:1 / 사용자도 M:1
 */

@Entity
@NoArgsConstructor
@Getter
public class Comment extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name="content",length = 512)
    @NotNull
    private String desc;

    @NotNull
    @Column(columnDefinition = "TINYINT",length = 1)
    private int star;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id",nullable = false)
    private Student writer;

    @ManyToOne
    @JoinColumn(name = "menu_id",nullable = false)
    private Menu menu;

    public Comment(CommentForm form) {
        this.desc = form.getDesc();
        this.star = Integer.parseInt(form.getStar());
    }

    public void setRelations(Student student, Menu menu) {
        student.getComments().add(this);
        menu.getComments().add(this);
        this.writer = student;
        menu.addStar(star);
        this.menu = menu;
    }

    @PreRemove
    public void delComment(){
        this.menu.delStar(star);
    }
}
