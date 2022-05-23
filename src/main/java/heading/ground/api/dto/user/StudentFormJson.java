package heading.ground.api.dto.user;

import lombok.Data;

@Data
public class StudentFormJson {

    private String loginId;
    private String email;
    private String name;
    private String phoneNumber;
    private String password;

}
