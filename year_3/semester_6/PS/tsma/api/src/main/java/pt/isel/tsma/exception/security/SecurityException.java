package pt.isel.tsma.exception.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.isel.tsma.exception.model.Code;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityException extends RuntimeException {

	private Code code = Code.UNAUTHORIZED;

	public SecurityException(String message) {
		super(message);
	}
}
