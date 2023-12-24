package pt.isel.tsma.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.isel.tsma.exception.model.Code;

import static pt.isel.tsma.exception.model.Code.RESOURCE_NOT_FOUND;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceNotFoundException extends RuntimeException {

	private Code code = RESOURCE_NOT_FOUND;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Code code, String message) {
		super(message);
		this.code = code;
	}
}
