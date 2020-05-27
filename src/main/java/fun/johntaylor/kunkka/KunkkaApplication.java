package fun.johntaylor.kunkka;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("fun.johntaylor.kunkka.mapper")
public class KunkkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KunkkaApplication.class, args);
    }

}
