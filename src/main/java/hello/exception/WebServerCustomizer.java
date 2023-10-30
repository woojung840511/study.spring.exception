package hello.exception;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/*
# 스프링 부트 - 오류 페이지1
스프링 부트가 제공하는 기본 오류 메커니즘을 사용하도록
WebServerCustomizer에 있는 @Component 를 주석 처리하자.

 @Component
 */

public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        // 1. http 상태코드
        // ErrorPage 생성자의 path는 Controller path
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        // 2. exeption이 발생했을 때
        // 다른 상세한 exception class에 대해서도 페이지를 설정할 수 있다.
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        factory.addErrorPages(
                errorPage404,
                errorPage500,
                errorPageEx
        );

        /*
        테스트 해보자.
        http://localhost:8080/error-ex
        http://localhost:8080/error-404
        http://localhost:8080/error-500
         */
    }

    /*

    3. 스프링 부트가 제공하는 오류페이지

    지금까지 예외 처리 페이지를 만들기 위해서 다음과 같은 복잡한 과정을 거쳤다.
        1) WebServerCustomizer 를 만들고
        2) 예외 종류에 따라서 ErrorPage 를 추가하고
        3) 예외 처리용 컨트롤러 ErrorPageController 를 만듦

    그러나, 스프링 부트는 이런 과정을 모두 기본으로 제공한다.
    - ErrorPage 를 자동으로 등록한다. 이때 /error 라는 경로로 기본 오류 페이지를 설정한다.
      - new ErrorPage("/error") , 상태코드와 예외를 설정하지 않으면 기본 오류 페이지로 사용된다.
      - 서블릿 밖으로 예외가 발생하거나, response.sendError(...) 가 호출되면
        모든 오류는 /error 를 호출하게 된다.
    - BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록한다.
      - ErrorPage 에서 등록한 /error 를 매핑해서 처리하는 컨트롤러다.

     [참고]
     ErrorMvcAutoConfiguration 이라는 클래스가 오류 페이지를 자동으로 등록하는 역할을 한다.

     [주의]
     스프링 부트가 제공하는 기본 오류 메커니즘을 사용하도록
     WebServerCustomizer에 있는 @Component 를 주석 처리하자.
     그러면,
     이제 오류가 발생했을 때 오류 페이지로 /error 를 기본 요청한다.
     스프링 부트가 자동 등록한 BasicErrorController 는 이 경로를 기본으로 받는다.

     ## 개발자는 오류 페이지만 등록
        BasicErrorController 는 기본적인 로직이 모두 개발되어 있다.
        개발자는 오류 페이지 화면만 BasicErrorController 가 제공하는 룰과 우선순위에 따라서
        등록하면 된다.
        - 정적 HTML이면 정적 리소스,
        - 뷰 템플릿을 사용해서 동적으로 오류 화면을 만들고 싶으면 뷰 템플릿 경로에,
        오류 페이지 파일을 만들어서 넣어두기만 하면 된다.

    ## 뷰 선택 우선순위 (BasicErrorController 의 처리 순서)

        1. 뷰템플릿
            resources/templates/error/500.html
            resources/templates/error/5xx.html
        2. 정적리소스(static,public)
            resources/static/error/400.html
            resources/static/error/404.html
            resources/static/error/4xx.html
        3. 적용 대상이 없을 때 뷰 이름(error)
            resources/templates/error.html

    해당 경로 위치에 HTTP 상태 코드 이름의 뷰 파일을 넣어두면 된다.
    - 뷰 템플릿이 정적 리소스보다 우선순위가 높고,
    - 404, 500처럼 구체적인 것이 5xx처럼 덜 구체적인 것 보다 우선순위가 높다.
    - 5xx, 4xx 라고 하면 500대, 400대 오류를 처리해준다.

     */
}
