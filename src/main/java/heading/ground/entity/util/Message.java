package heading.ground.entity.util;

import heading.ground.entity.Base;
import heading.ground.entity.user.BaseUser;
import heading.ground.forms.util.MsgForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Message extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private BaseUser writer;

    @ManyToOne
    @JoinColumn
    private BaseUser receiver;

    @Column(length = 16,nullable = false)
    private String title;

    @Lob
    @Column(name="body",length = 512)
    private String body;

    private boolean isRead;

    private String priorTitle;

    public Message(BaseUser writer, BaseUser receiver, MsgForm form) {
        this.writer = writer;
        this.receiver = receiver;
        title = form.getTitle();
        body = form.getBody();
        if(!form.getPriorMsg().isBlank()){
            priorTitle = form.getPriorMsg();
        }
    }

    public void read() {
        this.isRead = true;
    }
}
