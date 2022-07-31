package heading.ground.file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<ImageFile,String> {

    long deleteByStoreName(String storeName);
}
