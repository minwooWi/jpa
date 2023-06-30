package xyz.kbootcamp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.kbootcamp.config.auth.LoginUserArgumentResolver;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    /**
     * HandlerMethodArgumentResolver는 항상 WebMvcConfigurer의 addArgumentResolvers를 통해 추가
     *  :다른 HandlerMethodArgumentResolver가 필요하다면 동일한 방식으로 추가
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
//        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
