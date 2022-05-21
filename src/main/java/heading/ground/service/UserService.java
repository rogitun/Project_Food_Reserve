package heading.ground.service;

import heading.ground.dto.Paging;
import heading.ground.dto.user.SellerDto;
import heading.ground.entity.ImageFile;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Category;
import heading.ground.file.FileRepository;
import heading.ground.file.FileStore;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.SellerEditForm;
import heading.ground.forms.user.UserEditForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import heading.ground.security.user.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final FileRepository fileRepository;
    private final FileStore fileStore;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Value("${file.dir}")
    private String path;

    @Transactional
    public void updateSeller(Long id, SellerEditForm form) throws IOException {
        Seller seller = (Seller) userRepository.findById(id).get();
        Optional<Category> optCategory = categoryRepository.findByName(form.getCategory());
        //가게 데이터 변경 처리 + 파일처리
        //기본 필드, 사진, 카테고리

        seller.updateSeller(form,optCategory.orElseGet(null)); //필드 및 카테고리 변경
        if (seller.getImageFile() != null) { //이미 가진 사진이 있다면? -> 해당 사진 엔티티 찾고 파일 삭제 후 엔티티 삭제
            String storeName = seller.getImageFile().getStoreName();
            fileRepository.deleteByStoreName(storeName);
        }
        ImageFile imageFile = fileStore.storeFile(form.getImageFile());
        seller.updateImage(imageFile);
    }


    @Transactional
    public void updateStudent(Long id, UserEditForm form) {
        Student student = userRepository.findStudentById(id);
        student.update(form);
    }


    public Page<SellerDto> page(int s, int size) {
        PageRequest pageRequest = PageRequest.of(s, size);
        Page<Seller> all = userRepository.findAll(pageRequest);
        return all.map(se -> new SellerDto(se));
    }

    public Paging pageTemp(Page<SellerDto> page) {
        return new Paging(page.getTotalPages(), page.getNumber());
    }

    public String isValidated(BaseSignUp form, BindingResult bindingResult) {
        String returnUrl = form.getCompanyId() == null ? "/user/signup" : "/user/seller-signup";

        if (bindingResult.hasErrors()) { //필드 에러 처리
            return returnUrl;
        }

        if (!form.getPassword().equals(form.getPassword2())) {//비밀번호 다름(글로벌 에러 처리)
            bindingResult.reject("NotEquals", "비밀번호가 서로 일치하지 않습니다.");
            return returnUrl;
        }

        //TODO 서비스 처리 / 이미 가입된 아이디인지 체크
        long is_duplicated = userRepository.countByLoginId(form.getLoginId());
        if (is_duplicated > 0) {//중복된 아이디임;
            bindingResult.reject("Duplicate", "이미 가입된 아이디");
            return returnUrl;
        }
        return null;
    }

    @Transactional
    public void failedAttempt(Long id, boolean b) {
        BaseUser user = userRepository.findById(id).get();
        user.addFailed_attempt(b);
    }
}
