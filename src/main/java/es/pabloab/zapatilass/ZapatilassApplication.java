package es.pabloab.zapatilass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZapatilassApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZapatilassApplication.class, args);
    }

}
