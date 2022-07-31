package heading.ground.user.repository;

import heading.ground.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//TODO -> UserRepository로 통합
public interface StudentRepository extends JpaRepository<Student,Long> {

    Optional<Student> findByLoginId(String loginId);

    @Query("select distinct s from Student s " +
            "join fetch s.books b ")
    Student findByStudentWithBooks(@Param("id") Long id);

}
