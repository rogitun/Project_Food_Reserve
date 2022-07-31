package heading.ground.user.service;

import heading.ground.common.Paging;
import heading.ground.user.dto.SellerDto;
import heading.ground.file.ImageFile;
import heading.ground.user.entity.BaseUser;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import heading.ground.utils.entity.Category;
import heading.ground.file.FileRepository;
import heading.ground.file.FileStore;
import heading.ground.user.forms.SellerEditForm;
import heading.ground.user.forms.UserEditForm;
import heading.ground.user.repository.UserRepository;
import heading.ground.utils.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public void updateSeller(Long id, SellerEditForm form){
        Seller seller = (Seller) userRepository.findById(id).get();
        Optional<Category> optCategory = categoryRepository.findByName(form.getCategory());
        //가게 데이터 변경 처리 + 파일처리
        //기본 필드, 사진, 카테고리
        if(optCategory.isPresent()) {
            Category category = optCategory.get();
            seller.updateSeller(form,category);
        }else
            seller.updateSeller(form,null); //필드 및 카테고리 변경
//        if (seller.getImageFile() != null) { //이미 가진 사진이 있다면? -> 해당 사진 엔티티 찾고 파일 삭제 후 엔티티 삭제
//            String storeName = seller.getImageFile().getStoreName();
//            fileRepository.deleteByStoreName(storeName);
//        }
//        ImageFile imageFile = fileStore.storeFile(form.getImageFile());
//        seller.updateImage(imageFile);
    }
    @Transactional
    public void updatePhoto(Long id, MultipartFile image) throws IOException {
        Seller seller = (Seller) userRepository.findById(id).get();
        //가게 데이터 변경 처리 + 파일처리
        //기본 필드, 사진, 카테고리
        if (seller.getImageFile() != null) { //이미 가진 사진이 있다면? -> 해당 사진 엔티티 찾고 파일 삭제 후 엔티티 삭제
            String storeName = seller.getImageFile().getStoreName();
            fileStore.deleteImage(seller.getImageFile().getStoreName());
            fileRepository.deleteByStoreName(storeName);
        }
        ImageFile imageFile = fileStore.storeFile(image);
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
                .photo((se.getImageFile()!=null)?se.getImageFile().getStoreName():null)
                .build());
    }

    public Page<SellerDto> searchPage(int s,int size, String keyWord,String cat) {
        PageRequest pageRequest = PageRequest.of(s, size);
        Page<Seller> all = userRepository.findAllByKeyword(keyWord,cat,pageRequest);
        return all.map(se -> SellerDto.
                builder()
                .id(se.getId())
                .name(se.getName())
                .phoneNumber(se.getPhoneNumber())
                .desc(se.getDesc())
                .category((se.getCategory()!=null)?se.getCategory().getName():null)
                .photo((se.getImageFile()!=null)?se.getImageFile().getStoreName():null)
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
                .photo((se.getImageFile()!=null)?se.getImageFile().getStoreName():null)
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
