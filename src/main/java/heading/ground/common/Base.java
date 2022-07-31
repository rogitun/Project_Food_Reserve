package heading.ground.common;


import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Base {

    @CreatedDate
    @Column(updatable = false)
    protected String created;

    @LastModifiedDate
    protected String lastModified;

    @PrePersist
    public void onPrePersist(){
        created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        lastModified = created;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.lastModified = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }
}
