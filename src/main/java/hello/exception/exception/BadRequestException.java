package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 스프링 부트가 기본으로 제공하는 ExceptionResolver 는 다음과 같다. HandlerExceptionResolverComposite 에 다음 순서로 등록
 * 1. ExceptionHandlerExceptionResolver (@ExceptionHandler 처리)
 * 2. ResponseStatusExceptionResolver (Http 상태 코드를 지정)
 * 3. DefaultHandlerExceptionResolver (스프링 내부 기본 예외 처리) -> 우선 순위가 가장 낮다.
 *
 * 그중 2번 ResponseStatusExceptionResolver
 * ResponseStatusExceptionResolver 는 예외에 따라서 HTTP 상태 코드를 지정해주는 역할을 한다. 다음 두 가지 경우를 처리한다.
 * - @ResponseStatus 가 달려있는 예외
 * - ResponseStatusException 예외
 */


// @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
// reason 을 MessageSource 에서 찾는 기능도 제공한다.
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad")
public class BadRequestException extends RuntimeException{

}
