package pt.isel.tsma.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.isel.tsma.exception.model.Code;

import static pt.isel.tsma.exception.model.Code.UNKNOWN_ERROR;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidStateException extends RuntimeException {

	private Code code = UNKNOWN_ERROR;

	public InvalidStateException(String message) {
		super(message);
	}

	public InvalidStateException(Code code, String message) {
		super(message);
		this.code = code;
	}

	public InvalidStateException(Exception e, Code code, String message) {
		super(message, e);
		this.code = code;
	}
}
