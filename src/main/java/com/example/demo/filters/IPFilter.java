package com.example.demo.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@PropertySource(value = { "classpath:application.properties" })
public class IPFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(IPFilter.class);

  @Value("${allowed.ip}")
  private String allowedIp;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    final String ipAddress = request.getRemoteAddr();

    if (!isAllowed(ipAddress)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "IP address not allowed");
      return;
    }

    chain.doFilter(request, response);
  }

  private boolean isAllowed(String ipAddress) {
    log.info("allowedIp = " + allowedIp);

    if (StringUtils.isBlank(allowedIp)) {
      return true;
    } else {
      if (ipAddress.equals("0:0:0:0:0:0:0:1"))
        ipAddress = "127.0.0.1";

      ArrayList<String> ipList = new ArrayList<String>(Arrays.asList(allowedIp.split("\\|")));

      if (!ipList.contains(ipAddress)) {
        log.info(String.format("===== IP %s IS NOT ALLOWED =====", ipAddress));

        return false;
      } else {
        return true;
      }
    }
  }

  @Configuration
  public class AppConfig {
    @Bean
    public FilterRegistrationBean<IPFilter> ipFilter() {
      FilterRegistrationBean<IPFilter> registrationBean = new FilterRegistrationBean<>();
      registrationBean.setFilter(new IPFilter());
      registrationBean.addUrlPatterns("/*");
      return registrationBean;
    }
  }
}
