package es.pabloab.zapatillas.config;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.spring.PebbleViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

@Configuration
public class PebbleConfig {

    @Value("${pebble.suffix:.peb.html}")
    private String pebbleSuffix;

    @Value("${pebble.cache:false}")
    private boolean pebbleCache;

    @Value("${pebble.charset:UTF-8}")
    private String pebbleCharset;

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder()
                .loader(new ClasspathLoader())
                .cacheActive(pebbleCache)
                .build();
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver resolver = new PebbleViewResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(pebbleSuffix);
        resolver.setPebbleEngine(pebbleEngine());
        resolver.setCharacterEncoding(pebbleCharset);
        resolver.setOrder(1);
        return resolver;
    }
}
