package heading.ground.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Data
public class Paging {
    int start;
    int last;
    int totalPages;
    int number;
    String keyWord;

    @Builder
    public Paging(int total,int number,String key){
        this.totalPages = total;
        this.number = number;
        this.start = (int) Math.floor(number/10)*10+1;
        this.last = start+9 < totalPages ? start+9 : totalPages;

        if(key==null) this.keyWord="";
        else this.keyWord = key;
    }

}
