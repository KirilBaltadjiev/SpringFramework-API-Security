package edu.aubg.courseproject.server.filters.logging;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1) // Execute first
public class LoggingFilter extends OncePerRequestFilter implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

	private static final String NOTIFICATION_PREFIX = "* ";
	private static final String REQUEST_PREFIX = "> ";
	private static final String RESPONSE_PREFIX = "< ";

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		boolean isFirstRequest = !isAsyncDispatch(request);

		if (isFirstRequest) {

			request = new BodyCachingRequestWrapper(request, 500);
			response = new BodyCachingResponseWrapper(response, 500);
		}

		try {

			filterChain.doFilter(request, response);

		} finally {

			if (!isAsyncStarted(request)) {

				printRequest(request);
				printResponse(response);
			}
		}
	}

	private void printRequest(HttpServletRequest request) {

		StringBuilder sb = new StringBuilder();

		appendNotification(sb, NOTIFICATION_PREFIX, "Server has received a request");
		appendRequestInfo(sb, request);
		appendHeaders(sb, REQUEST_PREFIX, sortHeaders(Collections.list(request.getHeaderNames()), request::getHeader));

		BodyCachingRequestWrapper wrapper = (BodyCachingRequestWrapper) request;
		appendBody(sb, wrapper.getCachedBody(), wrapper.isWholeBodyCached());

		LOGGER.info(sb.append("\n").toString());
	}

	private void printResponse(HttpServletResponse response) {

		StringBuilder sb = new StringBuilder();

		appendNotification(sb, NOTIFICATION_PREFIX, "Server has responded");
		appendResponseInfo(sb, response);
		appendHeaders(sb, RESPONSE_PREFIX, sortHeaders(response.getHeaderNames(), response::getHeader));

		BodyCachingResponseWrapper wrapper = (BodyCachingResponseWrapper) response;
		appendBody(sb, wrapper.getCachedBody(), wrapper.isWholeBodyCached());

		LOGGER.info(sb.append("\n").toString());
	}

	private void appendNotification(StringBuilder sb, String prefix, String message) {

		sb.append("\n")
				.append(prefix)
				.append(message)
				.append(" on thread ")
				.append(Thread.currentThread().getName());
	}

	private void appendRequestInfo(StringBuilder sb, HttpServletRequest request) {

		sb.append("\n")
				.append(REQUEST_PREFIX)
				.append(request.getMethod())
				.append(" ")
				.append(request.getRequestURL());

		if (request.getQueryString() != null) {

			sb.append("?").append(request.getQueryString());
		}
	}

	private void appendResponseInfo(StringBuilder sb, HttpServletResponse response) {

		sb.append("\n").append(RESPONSE_PREFIX).append(response.getStatus());
	}

	private void appendHeaders(StringBuilder sb, String prefix, Map<String, String> headers) {

		headers.forEach((headerName, headerValue) -> {

			sb.append("\n").append(prefix).append(headerName).append(": ").append(headerValue);
		});
	}

	private void appendBody(StringBuilder sb, String body, boolean isWholeBody) {

		if (body != null) {

			sb.append("\n").append(body);

			if (!isWholeBody) {

				sb.append(" ... (more)");
			}
		}
	}

	private Map<String, String> sortHeaders(
			Collection<String> headerNames, Function<String, String> headerValueSupplier) {

		Map<String, String> headers = new HashMap<>();

		for (String header : headerNames) {

			headers.put(header, headerValueSupplier.apply(header));
		}

		return new TreeMap<>(headers);
	}
}
