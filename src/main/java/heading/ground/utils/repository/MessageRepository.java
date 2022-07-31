package heading.ground.utils.repository;


import heading.ground.utils.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("select m from Message m " +
            "join fetch m.receiver r " +
            "left join m.writer w " +
            "where w.id =:uid")
    List<Message> findSentMsg(@Param("uid") Long id,Pageable pageable);

    @Query("select m from Message m " +
            "join fetch m.writer w " +
            "left join m.receiver r " +
            "where r.id = :uid")
    List<Message> findReceivedMsg(@Param("uid") Long id, Pageable pageable);


    @EntityGraph(attributePaths = {"writer","receiver"})
    Optional<Message> findGraphById(Long id);


}
