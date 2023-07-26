package xyz.kbootcamp.web.dto;

import lombok.Getter;
import xyz.kbootcamp.domain.posts.Posts;
import java.time.format.DateTimeFormatter;

@Getter
public class PostsListsResponseDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private String modifiedDate;
    private Long cnt;

    public PostsListsResponseDto(Posts entity){
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.content = entity.getContent();
        this.cnt = entity.getCnt();
        this.modifiedDate = entity.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }
}
