package heading.ground.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.charset.StandardCharsets;

@Data
public class Paging {
    int start;
    int last;
    int totalPages;
    int number;
    String keyWord;
    String category;

    @Builder
    public Paging(int total,int number,String key,String cat){
        this.totalPages = total;
        this.number = number;
        this.start = (int) Math.floor(number/10)*10+1;
        this.last = start+9 < totalPages ? start+9 : totalPages;

        this.keyWord = key;
        this.category = cat;
    }
}
