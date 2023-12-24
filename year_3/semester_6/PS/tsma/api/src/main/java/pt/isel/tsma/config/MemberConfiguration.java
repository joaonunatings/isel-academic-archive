package pt.isel.tsma.config;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.service.member.MemberService;

import java.util.List;

@Configuration
@Slf4j(topic = "tsma")
public class MemberConfiguration {

	public static final List<String> VALID_SORT_PARAMS = List.of("id", "name", "email");
	private static ModelMapper modelMapper;

	@Bean(name = "memberModelMapper")
	public static ModelMapper modelMapper() {
		if (modelMapper != null) return modelMapper;

		modelMapper = new ModelMapper();
		return modelMapper;
	}

	@Profile("development")
	@Bean(name = "memberInitDatabase")
	@ConditionalOnProperty(value = "member.database.init", havingValue = "true")
	public ApplicationRunner initDatabase(MemberService memberService) {
		return args -> {
			log.debug("Initializing Member database...");
			char name = 'A';
			for (int i = 0; i < 14; i++) {
				var member = new MemberCreateDTO(String.valueOf(name), name + "@email.com");
				memberService.createMember(member);
				name = (char) (name + 1);
			}
			log.debug("Member database initialized");
		};
	}
}
