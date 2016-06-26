package com.goldCityWeb.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class LoginFailureHandler implements AuthenticationFailureHandler{

	private String defaultFailureUrl;
	private boolean forwardToDestination = false;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		String targetUrl = null;
        String type = (String) request.getSession().getAttribute("type");
        request.getSession().invalidate();
        
        if ("admin".equals(type)) {
        	targetUrl = "/login";
        } else {
        	targetUrl = "/merchant/login";
        }
        targetUrl = request.getContextPath() + targetUrl + "?error=error";
		if(forwardToDestination){
			request.getRequestDispatcher(targetUrl).forward(request, response);
		}else{
			response.sendRedirect(targetUrl);
		}
	}

	public String getDefaultFailureUrl() {
		return defaultFailureUrl;
	}
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}
	public boolean isForwardToDestination() {
		return forwardToDestination;
	}
	public void setForwardToDestination(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}
}
