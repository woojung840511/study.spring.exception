package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

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

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
