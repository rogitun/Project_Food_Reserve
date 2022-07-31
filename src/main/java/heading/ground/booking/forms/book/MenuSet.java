package heading.ground.booking.forms.book;

import lombok.Data;

@Data
public class MenuSet {
        private String name;
        private int quantity;

        public MenuSet(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

    }

