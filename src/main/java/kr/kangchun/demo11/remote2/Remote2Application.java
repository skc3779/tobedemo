package kr.kangchun.demo11.remote2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by skc37 on 2017-03-11.
 */
@SpringBootApplication
public class Remote2Application {

    @RestController
    public static class My2Controller {
        @RequestMapping(name="/service2", method= RequestMethod.GET)
        public String service1(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty("SERVER_PORT","8082");
        System.setProperty("server.tomcat.max-threads","200");
        SpringApplication.run(Remote2Application.class, args);
    }
}
