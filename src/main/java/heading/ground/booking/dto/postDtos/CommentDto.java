package heading.ground.booking.dto.postDtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {

    private Long id;
    private String writer;
    private String writerId;
    private String desc;
    private int star;
}
