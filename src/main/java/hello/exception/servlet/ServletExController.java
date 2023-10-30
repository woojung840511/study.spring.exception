package hello.exception.servlet;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


    /*
    서블릿은 다음 2가지 방식으로 예외 처리를 지원한다.
    1. Exception (예외)
    2. response.sendError(HTTP 상태 코드, 오류 메시지)
     */

@Slf4j
@Controller
public class ServletExController {


    /*
    1. Exception (예외)
    웹 브라우저에서 개발자 모드로 확인해보면 HTTP 상태 코드가 500으로 보인다.
    Exception 의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각해서 HTTP 상태 코드 500을 반환한다.
     */

    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생");
    }

    /*

    2. response.sendError(HTTP 상태 코드, 오류 메시지)

    오류가 발생했을 때 HttpServletResponse 가 제공하는 sendError 라는 메서드를 사용해도 된다.
    이것을 호출한다고 당장 예외가 발생하는 것은 아니지만,
    서블릿 컨테이너에게 오류가 발생했다는 점을 전달할 수 있다.
    이 메서드를 사용하면 HTTP 상태 코드와 오류 메시지도 추가할 수 있다.
        - response.sendError(HTTP 상태 코드)
        - response.sendError(HTTP 상태 코드, 오류 메시지)

    WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러
    (response.sendError())

    response.sendError() 를 호출하면 response 내부에는 오류가 발생했다는 상태를 저장해둔다.
    그리고 서블릿 컨테이너는 고객에게 응답 전에 response 에 sendError() 가 호출되었는지 확인한다.
    그리고 호출되었다면 설정한 오류 코드에 맞추어 기본 오류 페이지를 보여준다.

     */

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }
}
