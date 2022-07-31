package heading.ground.user.repository;


import heading.ground.user.entity.BaseUser;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepositoryCustom {

    Student findStudentById(Long id);

    Optional<BaseUser> findUserByIdEmail(String id, String email);

    Seller findByIdWithMenu(Long id);

    String findUserNameById(Long id);

    Page<Seller> findAllPage(Pageable pageRequest);

    Page<Seller> findAllByKeyword(String key,String cat, Pageable pageable);
}
