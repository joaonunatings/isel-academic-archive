package pt.isel.tsma.exception.handler;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.isel.tsma.exception.InvalidParameterException;
import pt.isel.tsma.exception.InvalidStateException;
import pt.isel.tsma.exception.ResourceNotFoundException;
import pt.isel.tsma.exception.security.SecurityException;

import java.util.Arrays;

import static pt.isel.tsma.exception.model.Code.INVALID_PARAMETER;
import static pt.isel.tsma.exception.model.Code.UNKNOWN_ERROR;

@ControllerAdvice
public class ControllerExceptionHandler {

	@Value("${server.error.print-stacktrace:false}")
	private boolean printStacktrace;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleExceptions(Exception e) {
		val status = HttpStatus.INTERNAL_SERVER_ERROR;
		val response = new ErrorResponse(status.value(), UNKNOWN_ERROR.getValue(), status.getReasonPhrase(), e.getMessage(), e.toString(), Arrays.toString(e.getStackTrace()));
		if (printStacktrace) e.printStackTrace();
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(InvalidParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<ErrorResponse> handleInvalidParameterException(InvalidParameterException ex) {
		val status = HttpStatus.BAD_REQUEST;
		val response = new ErrorResponse(status.value(), ex.getCode().getValue(), status.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		val status = HttpStatus.NOT_FOUND;
		val response = new ErrorResponse(status.value(), ex.getCode().getValue(), status.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(InvalidStateException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleInvalidStateException(InvalidStateException ex) {
		if (printStacktrace) ex.printStackTrace();
		val status = HttpStatus.INTERNAL_SERVER_ERROR;
		val response = new ErrorResponse(status.value(), ex.getCode().getValue(), status.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException ex) {
		val status = HttpStatus.BAD_REQUEST;
		val response = new ErrorResponse(status.value(), INVALID_PARAMETER.getValue(), status.getReasonPhrase(), ex.getLocalizedMessage());
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		val status = HttpStatus.BAD_REQUEST;
		val message = (ex.getMessage() == null) ? "Could not read JSON" : ex.getMessage();
		val response = new ErrorResponse(status.value(), INVALID_PARAMETER.getValue(), status.getReasonPhrase(), message);
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(SecurityException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(SecurityException ex) {
		val status = HttpStatus.UNAUTHORIZED;
		val response = new ErrorResponse(status.value(), ex.getCode().getValue(), status.getReasonPhrase(), ex.getMessage());
		return new ResponseEntity<>(response, status);
	}
}
