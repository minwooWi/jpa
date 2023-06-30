package xyz.kbootcamp.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.kbootcamp.service.posts.PostsService;
import xyz.kbootcamp.web.dto.PostsResponseDto;
import xyz.kbootcamp.web.dto.PostsSaveRequestDto;
import xyz.kbootcamp.web.dto.PostsUpdateRequestDto;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping("/posts")
    public Long save(@RequestBody PostsSaveRequestDto dto){
        return postsService.save(dto);
    }

    @GetMapping("/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id){
        return postsService.findById(id);
    }

    @PutMapping("/posts/{id}")
    public long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto reqDto){
        return postsService.update(id , reqDto);
    }

    @DeleteMapping("/posts/{id}")
    public Long delete(@PathVariable Long id){
        postsService.delete(id);
        return id;
    }
}
