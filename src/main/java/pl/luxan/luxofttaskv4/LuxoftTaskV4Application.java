package pl.luxan.luxofttaskv4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableJpaRepositories(basePackages="pl.luxan.luxofttaskv4")
public class LuxoftTaskV4Application {

    public static void main(String[] args) {
        SpringApplication.run(LuxoftTaskV4Application.class, args);
    }

}
