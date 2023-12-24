package pt.isel.tsma.security;

import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isel.tsma.exception.security.InvalidTokenException;
import pt.isel.tsma.exception.security.MissingTokenException;
import pt.isel.tsma.util.Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@ExtensionMethod(Utils.StringExtensions.class)
public class GraphAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	private final String AUTHORIZATION_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getServletPath().equals("/graph/sync")) {
			val authorizationHeader = extractAuthorizationHeader(request);
			val accessToken = extractAccessToken(authorizationHeader);
			setAuthentication(accessToken);
		}
		filterChain.doFilter(request, response);
	}

	private String extractAuthorizationHeader(HttpServletRequest request) {
		var authorization = "";
		try {
			authorization = request.getHeader(HEADER);
		} catch (Exception e) {
			throw new MissingTokenException();
		}
		if (authorization.isNullOrEmpty()) throw new MissingTokenException();
		if (!authorization.startsWith(AUTHORIZATION_PREFIX))
			throw new InvalidTokenException("Token is not " + AUTHORIZATION_PREFIX + "token");
		return authorization;
	}

	private String extractAccessToken(String authorizationHeader) {
		return authorizationHeader.substring(AUTHORIZATION_PREFIX.length());
	}

	private void setAuthentication(String accessToken) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, accessToken, List.of(new SimpleGrantedAuthority("consumers")));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
