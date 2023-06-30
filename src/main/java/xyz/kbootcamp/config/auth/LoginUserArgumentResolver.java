package xyz.kbootcamp.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.kbootcamp.config.auth.dto.SessionUser;
import javax.servlet.http.HttpSession;

/**
 * LoginUserArgumentResolver
 *  :HandlerMethodArgumentResolver 인터페이스를 구현한 클래스
 *   (위 인터페이스가 지원하는 한 가지 기능)
 *   ->조건에 맞는 경우 메소드가 있다면 HandlerMethodArgumentResolver의 구현체가 지정한 값으로 해당 메소드의 파라미터로 넘길 수 있음
 */
@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final HttpSession httpSession;

    /**
     * 컨트롤러 메소드의 특정 파라미터를 지원하는 판단하는 메소드
     * 특정 메소드 파라미터에 @LoginUser 어노테이션이 붙어 있고, 파라미터 클래스 타입이 SessionUser.class인 경우 ture return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    /**
     * 파라미터에 전달할 객체 생성
     * 세션에서 객체 조회
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }

    // 위와 같이 생성된 LoginUserArgumentResolver가 스프링에서 인식될 수 있도록 WebMvcConfigurer에 추가
}
