package heading.ground.dto.post;


import heading.ground.entity.post.Comment;
import lombok.Data;



@Data
public class CommentDto {

    private Long id;
    private String writer;
    private String writerId;
    private String desc;
    private int star;

    public CommentDto(Comment c) {
        this.id = c.getId();
        this.writer = c.getWriter().getName();
        this.desc = c.getDesc();
        this.star = c.getStar();
        this.writerId = c.getWriter().getLoginId();
    }
}
