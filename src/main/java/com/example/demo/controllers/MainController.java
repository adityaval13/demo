package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.MDCUtil;
import com.example.demo.utils.UtilHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@RestController
@RequestMapping
public class MainController {
  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  @Autowired
  MDCUtil mdc;

  @Autowired
  UtilHelper util;

  @Autowired
  JwtUtil jwtUtil;

  @PostMapping(path = "${api.path}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Object> mainApi(HttpServletRequest request, @RequestBody Map<String, Object> payload,
      @RequestHeader Map<String, Object> headers) {

    HttpHeaders headersResponse = new HttpHeaders();
    headersResponse.set("Encoding", "UTF-8");

    try {
      util.writeMessageForLogging(headers, "HEADERS");
      util.writeMessageForLogging(payload, "PAYLOAD");

      log.info("URL = " + request.getRequestURI());
      log.info("REMOTE ADDRESS = " + request.getRemoteAddr());

      String msg = (String) payload.get("msg");

      if (!jwtUtil.validateJwt(msg)) {
        log.info("JWT EXPIRED");

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(null);

        util.loggingResp(responseEntity, log);

        return responseEntity;
      }

      Map<String, Object> data = jwtUtil.getBodyFromJwt(msg);

      Claims claimsResponse = new DefaultClaims();

      String jwt = jwtUtil.generateJwt(util.mapToJson(data), claimsResponse);

      Map<String, Object> responseBody = new HashMap<>();

      responseBody.put("rc", "00");
      responseBody.put("rd", "success");
      responseBody.put("data", jwt);

      ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headersResponse).body(responseBody);

      util.loggingResp(responseEntity, log);

      return responseEntity;
    } catch (Exception e) {
      log.info(ExceptionUtils.getStackTrace(e));
    }

    ResponseEntity<Object> responseEntity = ResponseEntity.internalServerError().headers(headersResponse)
        .body(null);

    util.loggingResp(responseEntity, log);

    return responseEntity;
  }

  @GetMapping(path = "${api.path.get}", produces = "application/json")
  public ResponseEntity<Object> getApi(HttpServletRequest request,
      @RequestHeader Map<String, Object> headers) {
    HttpHeaders headersResponse = new HttpHeaders();
    headersResponse.set("Encoding", "UTF-8");

    try {
      util.writeMessageForLogging(headers, "HEADERS");

      log.info("URL = " + request.getRequestURI());
      log.info("REMOTE ADDRESS = " + request.getRemoteAddr());

      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("rc", "00");
      responseBody.put("rd", "success");

      ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headersResponse).body(responseBody);

      util.loggingResp(responseEntity, log);

      return responseEntity;
    } catch (Exception e) {
      log.info(ExceptionUtils.getStackTrace(e));
    }

    ResponseEntity<Object> responseEntity = ResponseEntity.internalServerError().headers(headersResponse).body(null);

    util.loggingResp(responseEntity, log);

    return responseEntity;
  }
}
