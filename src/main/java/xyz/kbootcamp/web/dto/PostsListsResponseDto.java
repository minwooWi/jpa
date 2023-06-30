package xyz.kbootcamp.web.dto;

import lombok.Getter;
import xyz.kbootcamp.domain.posts.Posts;

import java.time.LocalDateTime;

@Getter
public class PostsListsResponseDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public PostsListsResponseDto(Posts entity){
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate();
    }
}
