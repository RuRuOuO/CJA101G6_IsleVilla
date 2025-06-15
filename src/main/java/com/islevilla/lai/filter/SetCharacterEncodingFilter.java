package com.islevilla.lai.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import java.io.IOException;

public class SetCharacterEncodingFilter implements Filter {
	protected String encoding = null;
	protected FilterConfig config = null;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.encoding = config.getInitParameter(encoding);
	}

	@Override
	public void destroy() {
		this.encoding = null;
		this.config = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

}