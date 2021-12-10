package com.sprect;


import com.sprect.model.Role;
import com.sprect.model.StatusUser;
import com.sprect.model.entity.User;
import com.sprect.repository.sql.UserRepository;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories("com.sprect")
@SpringBootApplication
public class SecurApplication {

    private final String DESCRIPTION =
            "This is a training project that includes: \\\n" +
                    "  Registration, with the user saved in the database (PostgreSQL); \\\n" +
                    "  Authorization using Spring Security;\\\n" +
                    "  The basic operations CRUD implemented; \\\n" +
                    "  Support for a session using access and refresh tokens, for each of which an individual private key is generated, when working with the token, it gets blacklisted; \\\n" +
                    "  Sending emails to confirm mail or password resets; \\\n" +
                    "  NoSQL(REDIS) is used to store private keys, track authentication attempts; \\\n" +
                    "  Documentation is conducted using SpringDoc and swagger ui; \\\n" +
                    "  The project is covered by unit and integration tests; \\\n" +
                    "  It is possible to add and remove an avatar (images are stored on s3);";


    @Bean
    public OpenAPI customOpenAPI(@Value("${application-version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("sample application API")
                        .version(appVersion)
                        .description(DESCRIPTION)
                        .termsOfService("https://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));

    }

    private final UserRepository userRepository;

    public SecurApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public void initDefaultModerator() {
        User moderator = new User();
        moderator.setEmail("admin@mail.com");
        moderator.setRole(Role.MODERATOR);
        moderator.setStatus(StatusUser.ACTIVE);
        moderator.setPassword("$2a$10$lK1NwxZjME2McpzjNrx2P.pHFGJB/E0eTSbqUVgWmslbLp7iN4Jg.");//Qwer1234
        moderator.setUsername("admin");
        userRepository.save(moderator);
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurApplication.class, args);
    }
}
