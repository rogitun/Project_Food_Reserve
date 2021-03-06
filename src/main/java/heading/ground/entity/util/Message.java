package heading.ground.entity.util;

import heading.ground.entity.Base;
import heading.ground.entity.user.BaseUser;
import heading.ground.forms.util.MsgForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@Getter
public class Message extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private BaseUser writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private BaseUser receiver;

    @NotNull
    @Size(max = 30)
    private String title;

    @Lob
    @Column(name="body",length = 512)
    @NotNull
    private String body;

    @NotNull
    private boolean isRead;


    public Message(BaseUser writer, BaseUser receiver, MsgForm form) {
        this.writer = writer;
        this.receiver = receiver;
        title = form.getTitle();
        body = form.getBody();
    }

    public void read() {
        this.isRead = true;
    }
}
