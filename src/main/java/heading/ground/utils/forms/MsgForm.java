package heading.ground.utils.forms;

import heading.ground.utils.entity.Message;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MsgForm {

    @NotBlank
    @Length(max = 16)
    private String title;

    @NotBlank
    @Length(max = 500)
    private String body;

    @NotNull
    private Long senderId;

    @NotNull
    private Long receiverId;

    @NotNull
    private String receiverName;

    public void setIds(String name,Long Sid, Long Rid){
        receiverName = name;
        senderId = Sid;
        receiverId = Rid;
    }

    public void setReply(Message msg){
        receiverName = msg.getWriter().getName();
        senderId = msg.getReceiver().getId();
        receiverId = msg.getWriter().getId();
    }
}
