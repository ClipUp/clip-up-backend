package potenday.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Note {

    @Id
    private Long id;

    private String title;

    private String script;

    private String audioFileUrl;

    private String content;

    private Long createTime;

    private Long updateTime;

}
