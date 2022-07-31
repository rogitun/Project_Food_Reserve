package heading.ground.booking.dto.bookdtos;

import lombok.Data;

@Data
public class BookedMenuDto {

    private String menu;

    private int price;

    private int quantity;

    public BookedMenuDto(String name, int p, int q) {
        menu = name;
        price = p;
        quantity = q;
    }
}
