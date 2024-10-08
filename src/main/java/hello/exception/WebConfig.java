package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import hello.exception.resolver.MyHandlerExceptionResolver;
import hello.exception.resolver.UserHandlerExceptionResolver;
import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

    // @Bean // 인터셉터와 중복으로 처리되지 않기 위해 앞의 logFilter() 의 @Bean 에 주석을 달아두자. 풀면 필터도 로그 확인 가능
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

    /*

    # 서블릿 예외 처리 - 인터셉터

    앞서 필터의 경우에는 필터를 등록할 때 어떤 DispatcherType 인 경우에 필터를 적용할 지 선택할 수 있었다.
    그런데 인터셉터는 서블릿이 제공하는 기능이 아니라 스프링이 제공하는 기능이다.
    따라서 DispatcherType 과 무관하게 항상 호출된다.
    대신에 인터셉터는 다음과 같이 요청 경로에 따라서 추가하거나 제외하기 쉽게 되어 있기 때문에,
    이러한 설정을 사용해서 오류 페이지 경로를 excludePathPatterns 를 사용해서 빼주면 된다.

     */

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error" /*, "/error-page/**" */ ); // 오류 페이지 경로를 제외할 수 있다.
                // 여기에서 /error-page/** 를 제거(혹은 주석처리)하면 error-page/500 같은 내부 호출의 경우에도 인터셉터가 호출된다.
    }


    /**
     # 전체 흐름 정리

     ## /hello 정상 요청
        WAS(/hello, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러 -> View

     ## /error-ex 오류 요청
     - 필터는 DispatchType 으로 중복 호출 제거 ( dispatchType=REQUEST )
     - 인터셉터는 경로 정보로 중복 호출 제거( excludePathPatterns("/error-page/**") )

         1. WAS(/error-ex, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
         2. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
         3. WAS 오류 페이지 확인
         4. WAS(/error-page/500, dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) -> 컨트롤러(/error-page/500) -> View
     */

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandlerExceptionResolver());
        resolvers.add(new UserHandlerExceptionResolver());
    }

    /**
     * configureHandlerExceptionResolvers(..) 를 사용하면 스프링이 기본으로 등록하는 ExceptionResolver 가 제거되므로 주의,
     * extendHandlerExceptionResolvers 를 사용하자.
     */
}
