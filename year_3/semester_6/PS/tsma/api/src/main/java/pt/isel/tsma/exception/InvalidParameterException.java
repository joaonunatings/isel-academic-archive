package pt.isel.tsma.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.isel.tsma.exception.model.Code;

import static pt.isel.tsma.exception.model.Code.INVALID_PARAMETER;

@Data
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {

	private Code code = INVALID_PARAMETER;

	public InvalidParameterException(String message) {
		super(message);
	}

	public InvalidParameterException(Code code, String message) {
		super(message);
		this.code = code;
	}
}
