package heading.ground.dto.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MsgDto {

    private Long id;
    private String sender;
    private String receiver;

    private Long receiverId;
    private Long senderId;
    private String title;
    private String body;
    private boolean isRead;
    private String prior;

    private String created;

}
