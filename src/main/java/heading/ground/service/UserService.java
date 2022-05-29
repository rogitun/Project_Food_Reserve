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
import heading.ground.forms.user.SellerEditForm;
import heading.ground.forms.user.UserEditForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Page<Seller> all = userRepository.findAllPage(pageRequest);
        return all.map(se -> SellerDto.
                builder()
                .id(se.getId())
                .name(se.getName())
                .phoneNumber(se.getPhoneNumber())
                .desc(se.getDesc())
                .category((se.getCategory()!=null)?se.getCategory().getName():null)
                .build());
    }

    public Page<SellerDto> searchPage(int s,int size, String keyWord) {
        PageRequest pageRequest = PageRequest.of(s, size);
        Page<Seller> all = userRepository.findAllByKeyword(keyWord,pageRequest);
        return all.map(se -> SellerDto.
                builder()
                .id(se.getId())
                .name(se.getName())
                .phoneNumber(se.getPhoneNumber())
                .desc(se.getDesc())
                .category((se.getCategory()!=null)?se.getCategory().getName():null)
                .build());
    }

    public Page<SellerDto> searchCategory(int s,int size, String cat) {
        PageRequest pageRequest = PageRequest.of(s, size);
        Page<Seller> all = userRepository.findAllByCategory(cat,pageRequest);
        return all.map(se -> SellerDto.
                builder()
                .id(se.getId())
                .name(se.getName())
                .phoneNumber(se.getPhoneNumber())
                .desc(se.getDesc())
                .category((se.getCategory()!=null)?se.getCategory().getName():null)
                .build());
    }


    public Paging pageTemp(Page<SellerDto> page,String key,String cat) {
        Paging build = Paging.builder()
                .total(page.getTotalPages())
                .number(page.getNumber())
                .key(key)
                .cat(cat)
                .build();
        return build;
    }

    @Transactional
    public void failedAttempt(Long id, boolean b) {
        BaseUser user = userRepository.findById(id).get();
        user.addFailed_attempt(b);
    }

    public ResponseEntity<String> isValidated(String input, String name) {
        long count=0;
        if(name.equals("아이디")){
            count = userRepository.countByLoginId(input);

        }
        else if(name.equals("이메일")){
            count = userRepository.countByEmail(input);
        }

        if(count==0){
            return new ResponseEntity<String>("사용 가능한 "+name,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<String>("이미 사용중인 "+name, HttpStatus.FORBIDDEN);
        }
    }


}
