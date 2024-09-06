package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html
 *
 * @ControllerAdvice
 * @ControllerAdvice 는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler , @InitBinder 기능을 부여해주는 역할을 한다.
 * @ControllerAdvice 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)
 * @RestControllerAdvice 는 @ControllerAdvice 와 같고, @ResponseBody 가 추가되어 있다.
 * @Controller , @RestController 의 차이와 같다.
 */

@Slf4j
@RestControllerAdvice(basePackages = "hello.exception.api") // 특정 패키지에만 적용할 수도 있다.
/*
특정 애노테이션이 있는 컨트롤러를 지정할 수 있고, 특정 패키지를 직접 지정할 수도 있다.
패키지 지정의 경우 해당 패키지와 그 하위에 있는 컨트롤러가 대상이 된다. 그리고
특정 클래스를 지정할 수도 있다.
 */

public class ExControllerAdvice {
    /**
     * @ExceptionHandler
     * 스프링은 API 예외 처리 문제를 해결하기 위해 @ExceptionHandler 라는
     * 애노테이션을 사용하는 매우 편리한 예외 처리 기능을 제공하는데,
     * 이것이 바로 ExceptionHandlerExceptionResolver 이다.
     *
     * 스프링은 ExceptionHandlerExceptionResolver 를 기본으로 제공하고,
     * 기본으로 제공하는 ExceptionResolver 중에 우선순위도 가장 높다.
     * 실무에서 API 예외 처리는 대부분 이 기능을 사용한다.
     *
     * @ExceptionHandler 예외 처리 방법
     * @ExceptionHandler 애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.
     * 해당 컨트롤러에서 예외가 발생하면 이 메서드가 호출된다.
     * 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.
     *
     * pdf 파일 꼭 참고할 것 (오류 우선순위, 다양한 예외 처리 방법)
     *
     * 다양한 예외처리 방법 예:
     * @ExceptionHandler({AException.class, BException.class})
     * public String ex(Exception e) {
     *      log.info("exception e", e);
     * }
     *
     * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html#mvc-ann-exceptionhandler-args
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 이렇게 상태코드를 변경할 수도 있다.
    @ExceptionHandler(IllegalArgumentException.class)
    // IllegalArgumentException 또는 그 하위 자식 클래스를 모두 처리할 수 있다.
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    /*
    예외 생략
        @ExceptionHandler 에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }
}
