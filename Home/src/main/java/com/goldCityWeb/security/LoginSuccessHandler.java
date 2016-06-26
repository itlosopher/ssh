/**
 * 
 */
package com.goldCityWeb.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.goldCityWeb.dao.SysUsersDao;
import com.goldCityWeb.domain.SysUsers;



/**
 * @author Administrator
 * 
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler, InitializingBean {
	@Autowired
	private SysUsersDao sysUsersDao;

	protected Log logger = LogFactory.getLog(getClass());

	private boolean forwardToDestination = false;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.authentication.AuthenticationSuccessHandler
	 * #onAuthenticationSuccess(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		SysUsers ud = (SysUsers) authentication.getPrincipal();
		String targetUrl = null;

        if (ud.getType() == 3) {
            targetUrl = "/merchant/index";
        } else {
            targetUrl = "/index";
        }

        request.getSession().setAttribute("sessionUser", ud);

		if (this.forwardToDestination) {
			logger.info("Login success,Forwarding to " + targetUrl);
			request.getRequestDispatcher(targetUrl).forward(request, response);
		} else {
			logger.info("Login success,Redirecting to " + targetUrl);
			this.redirectStrategy.sendRedirect(request, response, targetUrl);
		}
	}

	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public void setForwardToDestination(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

}
