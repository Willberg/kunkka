package fun.johntaylor.kunkka;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@MapperScan("fun.johntaylor.kunkka.repository.mybatis")
@EnableWebFlux
public class KunkkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KunkkaApplication.class, args);
    }

}
