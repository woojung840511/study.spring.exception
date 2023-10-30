package hello.exception;

import hello.exception.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;


@Configuration
public class WebConfig implements WebMvcConfigurer {

   /*

    # 서블릿 예외 처리 - 필터

    ## 목표
        예외 처리에 따른 필터와 인터셉터 그리고 서블릿이 제공하는 DispatchType 이해하기

    ## 예외 발생과 오류 페이지 요청 흐름
        1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
        2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error- page/500) -> View

        오류가 발생하면 오류 페이지를 출력하기 위해 WAS 내부에서 다시 한번 호출이 발생한다.
        이때 필터, 서블릿, 인터셉터도 모두 다시 호출된다.
        그런데 로그인 인증 체크 같은 경우를 생각해보면, 이미 한번 필터나, 인터셉터에서 로그인 체크를 완료했다.
        따라서 서버 내부에서 오류 페이지를 호출한다고 해서 해당 필터나 인터셉트가 한번 더 호출되는 것은 매우 비효율적이다.
        결국 클라이언트로 부터 발생한 정상 요청인지, 아니면 오류 페이지를 출력하기 위한 내부 요청인지 구분할 수 있어야 한다.
        서블릿은 이런 문제를 해결하기 위해 DispatcherType 이라는 추가 정보를 제공한다.

        참고 - 필터는 서블릿 기술, 인터셉터는 스프링 기술

    ## DispatcherType
       서블릿 스펙은 실제 고객이 요청한 것인지, 서버가 내부에서 오류 페이지를 요청하는 것인지 DispatcherType 으로 구분할 수 있는 방법을 제공한다.

        public enum DispatcherType {
            FORWARD,  // MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때 RequestDispatcher.forward(request, response);
            INCLUDE,  // 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때  RequestDispatcher.include(request, response);
            REQUEST,  // 클라이언트 요청
            ASYNC,    // 서블릿 비동기 호출
            ERROR     // 오류 요청
        }
    */

    @Bean
    public FilterRegistrationBean logFilter() {

        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter()); // 로그 필터 추가
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");


        /*
        이렇게 두 가지를 모두 넣으면 클라이언트 요청은 물론이고, 오류 페이지 요청에서도 필터가 호출된다.
        아무것도 넣지 않으면 기본 값이 DispatcherType.REQUEST 이다.
        즉 클라이언트의 요청이 있는 경우에만 필터가 적용된다.
        특별히 오류 페이지 경로도 필터를 적용할 것이 아니면, 기본 값을 그대로 사용하면 된다.
        물론 오류 페이지 요청 전용 필터를 적용하고 싶으면 DispatcherType.ERROR 만 지정하면 된다.
         */
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);


        return filterRegistrationBean;
    }
}
