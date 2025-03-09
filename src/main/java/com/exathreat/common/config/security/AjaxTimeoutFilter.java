package com.exathreat.common.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class AjaxTimeoutFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
		
		if (req.getRequestedSessionId() != null && !req.isRequestedSessionIdValid()) {
			String ajaxHeader = req.getHeader("X-Requested-With");
			if ("XMLHttpRequest".equals(ajaxHeader)) {
				res.sendError(901, "SESSION_TIMED_OUT");
				return;
			}
		}
		chain.doFilter(request, response);
	}
	
}