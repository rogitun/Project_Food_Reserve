package heading.ground.user.dto;

import heading.ground.booking.entity.book.BookStatus;
import heading.ground.booking.entity.book.BookType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserBookDto {
    private String id;
    private BookStatus status;

    private BookType type; //방문 타입

    private int totalPrice;

    private LocalDateTime bookTime;

    private String reason;

    private int number; //사람 몇명 오는지

    private String student; //예약자

    private boolean isPaid;

    private SellerDto seller;

    public String type(){
        if(type==BookType.HERE) return "매장";
        else if(type==BookType.TOGO) return "포장";
        else return "미정";
    }

    public String status(){
        if(isPaid == false){
            return "미결제";
        }

        String statusIs;
        if(status==BookStatus.PENDING)
            statusIs = "승인 대기중";
        else if(status==BookStatus.ACCEPT)
            statusIs ="예약 승인";
        else if(status==BookStatus.WITHDRAW)
            statusIs = "사용자로부터 취소 요청";
        else
            statusIs = "예약 취소";

        return statusIs;
    }

    public boolean process(){
        if(status==BookStatus.ACCEPT || status==BookStatus.CANCELED)
            return true;
        else return false;
    }

}
