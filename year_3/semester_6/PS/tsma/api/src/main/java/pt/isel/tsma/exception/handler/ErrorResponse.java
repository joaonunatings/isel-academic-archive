package pt.isel.tsma.exception.handler;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ErrorResponse {

	private LocalDateTime timestamp = LocalDateTime.now();

	@NonNull
	private final Integer status;

	private final Integer code;

	@NonNull
	private final String error;

	@NonNull
	private final String message;

	private String exception;

	private String stacktrace;

	private Object data;

	public ErrorResponse(Integer status, Integer code, @NonNull String error, @NonNull String message, String exception, String stacktrace) {
		this(status, code, error, message);
		this.exception = exception;
		this.stacktrace = stacktrace;
	}
}
