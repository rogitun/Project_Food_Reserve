package heading.ground.api;

import heading.ground.api.dto.user.StudentFormJson;
import heading.ground.entity.user.MyRole;
import heading.ground.entity.user.Seller;
import heading.ground.api.dto.user.SellerFormJson;
import heading.ground.entity.user.Student;
import heading.ground.repository.user.UserRepository;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserSignUpController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup-student")
    public ResponseEntity<String> signUpStudent(@RequestBody StudentFormJson student){
        long l = userRepository.countEmailId(student.getLoginId(), student.getEmail());
        if(l>0)
            return new ResponseEntity<>("아이디 혹은 이메일 중복확인 필요",HttpStatus.FORBIDDEN);
        else{
            Student newStudent = Student.builder()
                    .id(student.getLoginId())
                    .email(student.getEmail())
                    .name(student.getName())
                    .phone(student.getPhoneNumber())
                    .pwd(passwordEncoder.encode(student.getPassword()))
                    .build();
            userRepository.save(newStudent);

            return new ResponseEntity<>("회원가입 성공",HttpStatus.OK);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SellerFormJson seller) {
        long l = userRepository.countEmailId(seller.getLoginId(), seller.getEmail());
        if(l>0){
            return new ResponseEntity<>("아이디 및 이메일 중복확인 해주세요.",HttpStatus.FORBIDDEN);
        }
        else{
            Seller newSeller = Seller.builder()
                    .loginId(seller.getLoginId())
                    .name(seller.getName())
                    .password(passwordEncoder.encode(seller.getPassword()))
                    .phoneNumber(seller.getPhoneNumber())
                    .email(seller.getEmail())
                    .role(MyRole.SELLER)
                    .doro(seller.getDoro())
                    .doroSpce(seller.getDoroSpec())
                    .zipCode(seller.getZipCode())
                    .companyId(seller.getCompanyId())
                    .build();
            userRepository.save(newSeller);
            return new ResponseEntity<>("가입 완료",HttpStatus.OK);
        }
    }

    @PostMapping("/signup/validatingEmail")
    public ResponseEntity<String> email(@RequestBody HashMap<String, String> email){
        ResponseEntity<String> validated = userService.isValidated(email.get("email"), "이메일");
        return validated;
    }

    @PostMapping("/signup/validatingId")
    public ResponseEntity<String> id(@RequestBody HashMap<String, String> id){
        ResponseEntity<String> validated = userService.isValidated(id.get("id"), "아이디");
        return validated;
    }
}
