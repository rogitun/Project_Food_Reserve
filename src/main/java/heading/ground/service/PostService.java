package heading.ground.service;

import heading.ground.dto.post.MenuDto;
import heading.ground.dto.Paging;
import heading.ground.entity.ImageFile;
import heading.ground.entity.post.Comment;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.file.FileRepository;
import heading.ground.file.FileStore;
import heading.ground.forms.post.MenuForm;
import heading.ground.repository.post.CommentRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.SellerRepository;
import heading.ground.repository.user.StudentRepository;
import heading.ground.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//글과 댓글(리뷰)등을 관리하는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final MenuRepository menuRepository;
    private final FileRepository fileRepository;
    private final FileStore fileStore;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addMenu(MenuForm form, Long id) throws IOException {
        //Seller seller = sellerRepository.findById(id).get();
        Seller seller = (Seller) userRepository.findById(id).get();
        Menu menu = form.toEntity();

        MultipartFile image = form.getImage();
        if(!image.isEmpty()){ //이미지 있으면
            ImageFile imageFile = fileStore.storeFile(image);
            menu.addImage(imageFile);
        }
        menu.addSeller(seller);
        menuRepository.save(menu);
    }

    @Transactional
    public void updateMenu(Menu menu,MenuForm form) throws IOException {
        menu.updateField(form);
        //이미지 체크
        if(!form.getImage().isEmpty()){
            MultipartFile image = form.getImage();
            ImageFile imageFile = fileStore.storeFile(image);

            if(menu.getImage()!=null){ //기존 이미지가 있으면 삭제함.
                String imageId = menu.getImage().getId();
                fileRepository.deleteById(imageId);
                fileStore.deleteImage(menu.getImage().getStoreName());
            }
            menu.addImage(imageFile);
        }
    }

    @Transactional //메뉴, 학생, 댓글 연관관계 세팅
    public void addComment(Long studentId, Comment comment, Long id) {
        Menu menu = menuRepository.findById(id).get();
       // Student student = studentRepository.findById(studentId).get();
        Student student = (Student) userRepository.findById(studentId).get();
        comment.setRelations(student,menu);
        commentRepository.save(comment);
    }

    public Page<MenuDto> page(int s, int n) {
        PageRequest pageRequest = PageRequest.of(s,n);
        Page<Menu> all = menuRepository.findAll(pageRequest);
        return all.map(a -> new MenuDto(a));
    }

    public Page<MenuDto> pageBySeller(int s, int n,Long id) {
        PageRequest pageRequest = PageRequest.of(s,n);
        Page<Menu> all = menuRepository.findBySellerPage(id, pageRequest);
        return all.map(a -> new MenuDto(a));
    }

    public Paging pageTemp(Page<MenuDto> page){
        return new Paging(page.getTotalPages(), page.getNumber());
    }



    @Transactional
    public void setMenuStatus(Long id, String flag) {
        Menu menu = menuRepository.findById(id).get();
        log.info("service in");
        if(flag.equals("best")) {
            menu.setBest();
        }
        else if(flag.equals("stock")) {
            menu.setStock();
        }
    }

}
