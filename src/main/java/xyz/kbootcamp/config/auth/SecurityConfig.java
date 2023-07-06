package xyz.kbootcamp.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import xyz.kbootcamp.domain.user.Role;

@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정 Activated
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /**
                 * .csrf().disable()
                 * .headers().frameOptions().disable()
                 *  ->h2-console 화면을 사용하기 위해 해당 옵션들을 disable
                 */
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                /**
                 * authorizeRequests()
                 *  ->URL별 권한 관리를 설정하는 옵션의 시작점
                 *  ->authorizeRequests가 선언되야만 antMatchers 옵션 사용이 가능
                 */
                .authorizeRequests()
                /**
                 * antMatchers
                 *  ->권한 관리 대상을 지정하는 옵션
                 *  ->URL, HTTP 메소드 별로 관리가 가능
                 *  ->"/" 등 지정된 URL들은 permitAll() 옵션을 통해 전체 열람 권한 부여
                 *  ->"/api/v1/**" 주소를 가진 API는 USER 권한을 가진 사람만 가능하도록 설정
                 */
                .antMatchers("/", "/profile", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile", "/horseList", "/excel", "/favicon.ico", "/error")
                .permitAll()
                .antMatchers("/posts/**").hasRole(Role.USER.name())
                /**
                 * anyRequest
                 *  ->설정된 값들 이외 나머지 URL들을 표시
                 *  ->여기서는! authenticated() 를 추가하여 나머지 URL들은 모두 인증된 사용자들에게만 허용
                 *  ->인증된 사용자 즉, 로그인한 사용자들
                 */
                .anyRequest().authenticated()
                .and()
                /**
                 * logout().logoutSuccessURL("/")
                 *  ->로그아웃 기능에 대한 여러 설정의 진입점(StartPoint?)
                 *  ->로그아웃 성공 시 / 주소로 이동
                 */
                .logout()
                .logoutSuccessUrl("/")
                .and()
                /**
                 * oauth2Login()
                 *  ->OAuth2 로그인 기능에 대한 여러 설정의 진입점
                 */
                .oauth2Login()
                /**
                 * userInfoEndpoint()
                 *  ->OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                 */
                .userInfoEndpoint()
                /**
                 * userService
                 *  ->소셜 로그인 성공 시 후속 조치를 진행 할 UserService 인터페이스의 구현체를 등록
                 *  ->Resourse Server(소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 또한 명시 가능
                 */
                .userService(customOAuth2UserService);

        return http.build();
    }
}
