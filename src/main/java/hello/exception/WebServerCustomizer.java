package hello.exception;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
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
}
