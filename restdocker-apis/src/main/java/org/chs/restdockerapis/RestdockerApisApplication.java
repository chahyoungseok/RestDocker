package org.chs.restdockerapis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "org.chs")
public class RestdockerApisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestdockerApisApplication.class, args);
    }

}
