package heading.ground.forms.book;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookForm {
    //방문일자, (포장or매장), 방문인원

    private LocalDate date;
    private LocalTime time;
    private String type;
    private int number;

}
