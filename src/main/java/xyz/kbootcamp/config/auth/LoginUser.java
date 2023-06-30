package xyz.kbootcamp.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target(ElementType.PARAMETER)
 *  :해당 어노테이션이 생성될 수 있는 위치 지정
 *  :ElementType을 PARAMETER로 지정했으니 메소드의 파라미터로 선언된 객체에서만 사용 가능
 *  :이 외에도 클래스 선언문에 쓸 수 있는 TYPE등이 있음
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @interface
 *  :LoginUser java 파일을 어노테이션 클래스로 지정
 */
public @interface LoginUser {
}

// 200Page ----------------->