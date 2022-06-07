package heading.ground.entity.util;

import heading.ground.entity.Base;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 25)
    private String title;

    @NotNull
    @Lob
    @Column(length = 1024)
    private String content;

    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void edit(String title, String content){
        this.title = title;
        this.content = content;
    }
}
