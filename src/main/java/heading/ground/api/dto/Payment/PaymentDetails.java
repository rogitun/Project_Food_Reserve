package heading.ground.api.dto.Payment;

import heading.ground.booking.entity.book.Book;
import lombok.Data;

@Data
public class PaymentDetails {
    private String bookId;
    private String sellerName;
    private String studentName;
    private String email;
    private String phoneNumber;
    private int paidAmount;

    public PaymentDetails(Book book) {
        this.bookId = book.getId();
        this.sellerName = book.getSeller().getName();
        this.studentName = book.getStudent().getName();
        this.email = book.getStudent().getEmail();
        this.phoneNumber = book.getStudent().getPhoneNumber();
        this.paidAmount = book.getTotalPrice();
    }
}
