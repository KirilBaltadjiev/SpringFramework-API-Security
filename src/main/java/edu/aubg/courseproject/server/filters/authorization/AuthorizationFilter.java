package edu.aubg.courseproject.server.filters.authorization;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.aubg.courseproject.server.exceptions.ServerStartUpException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.X509VerificationKeyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.security.x509.X509CertImpl;

@Component
@Order(2) // Execute after the logging filter; overriden by FilterRegistrationBean in Application
public class AuthorizationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

	private static final Pattern AUTHORIZATION_PATTERN =
			Pattern.compile("^[bB][eE][aA][rR][eE][rR] +([a-zA-Z0-9/+~_.-]+=*)$");

	private final JwtConsumer jwtConsumer;

	public AuthorizationFilter() {

		X509VerificationKeyResolver keyResolver;

		try (InputStream is = AuthorizationFilter.class.getClassLoader().getResourceAsStream("jwt/server.crt")) {

			keyResolver = new X509VerificationKeyResolver(new X509CertImpl(is));

		} catch (IOException | CertificateException ex) {

			throw new ServerStartUpException("Could not initialize Authorization interceptor", ex);
		}

		keyResolver.setTryAllOnNoThumbHeader(true);

		jwtConsumer = new JwtConsumerBuilder()
				.setRequireExpirationTime()
				.setEnableRequireIntegrity()
				.setSkipDefaultAudienceValidation()
				.setVerificationKeyResolver(keyResolver)
				.setRequireSubject()
				.setRequireNotBefore()
				.setRequireExpirationTime()
				.setRequireIssuedAt()
				.build();
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null) {

			invalidTokenError(response, "Missing Authorizaiton header value.");
		}

		Matcher authorizationMatcher = AUTHORIZATION_PATTERN.matcher(authorizationHeader);

		if (!authorizationMatcher.matches()) {

			invalidTokenError(response, "Invalid Authorization header value.");
		}

		String token = authorizationMatcher.group(1);

		try {

			jwtConsumer.process(token);

		} catch (InvalidJwtException ex) {

			invalidTokenError(response, ex.getMessage());
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void invalidTokenError(HttpServletResponse response, String errorDescription) throws IOException {

		ErrorMessage errorMessage = new ErrorMessage("Invalid token", errorDescription);

		ObjectMapper mapper = new ObjectMapper();
		String errorJson = mapper.writeValueAsString(errorMessage);

		response.getOutputStream().write(errorJson.getBytes());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON.toString());

		LOGGER.warn("Reject	ed access with the following error description: " + errorDescription);
	}

	public static class ErrorMessage {

		private final String error;

		private final String description;

		public ErrorMessage(String error, String description) {

			this.error = error;
			this.description = description;
		}

		public String getError() {

			return error;
		}

		public String getDescription() {

			return description;
		}
	}
}
