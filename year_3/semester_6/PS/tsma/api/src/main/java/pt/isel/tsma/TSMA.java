package pt.isel.tsma;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "Team Shift Management API", version = "v1"))
public class TSMA {

	public static void main(String[] args) {
		SpringApplication.run(TSMA.class, args);
	}
}