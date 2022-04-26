package heading.ground.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.File;

@Entity
@NoArgsConstructor
@Getter
public class ImageFile {

    @Id @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2",strategy = "uuid2")
    private String id;

    private String originName;

    private String storeName;

    public ImageFile(String uploadName, String storeName) {
        this.originName = uploadName;
        this.storeName = storeName;
    }

    @PreRemove
    public void deleteImageFile(){
        File file = new File(FileConst.Dir + this.storeName);
        file.delete();
    }

}
