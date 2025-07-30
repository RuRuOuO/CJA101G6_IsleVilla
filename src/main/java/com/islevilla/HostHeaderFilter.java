package com.islevilla;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HostHeaderFilter implements Filter {

	@Value("${app.allowed-hosts}")
	private String[] allowedHosts;

	private static final Logger logger = Logger.getLogger(HostHeaderFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String hostHeader = httpRequest.getHeader("Host");
		String remoteAddr = httpRequest.getRemoteAddr();

		List<String> allowedHostList = Arrays.asList(allowedHosts);

		logger.info("Incoming request from IP: " + remoteAddr + " with Host: " + hostHeader);

		if (hostHeader == null || !allowedHostList.contains(hostHeader)) {
			// 這裡把可疑來源印出來
			logger.warning("Blocked request from IP: " + remoteAddr + " with Host: " + hostHeader);

			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
			return;
		}

		chain.doFilter(request, response);
	}
}
