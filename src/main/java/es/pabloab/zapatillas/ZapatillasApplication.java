package es.pabloab.zapatillas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZapatillasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZapatillasApplication.class, args);
    }
}
