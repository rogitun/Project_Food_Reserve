package heading.ground.entity.util;

import heading.ground.entity.user.Seller;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotNull
    @Size(max = 5)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Seller> seller = new LinkedList<>();

    public Category(String name) {
        this.name = name;
    }

}
