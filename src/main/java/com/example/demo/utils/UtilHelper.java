package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UtilHelper {
  private static final Logger log = LoggerFactory.getLogger(UtilHelper.class);

  public void writeMessageForLogging(Map<String, Object> hm, String msg) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString(hm);
      log.info(msg + " = " + json);
    } catch (Exception e) {
      log.info(msg + " = " + hm.toString());
      log.info(ExceptionUtils.getStackTrace(e));
    }
  }

  public void loggingResp(final Object object, Logger logger) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      log.info("RESPONSE = " + mapper.writeValueAsString(object));
    } catch (Exception e) {
      log.info(e.toString());
      log.info("RESPONSE = " + object.toString());
    }
  }

  public String mapToJson(Map<String, Object> map) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = objectMapper.writeValueAsString(map);

    return jsonString;
  }

  public Map<String, Object> jsonToMap(String str) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      JSONParser parser = new JSONParser();
      Object obj = parser.parse(str);
      JSONObject jsonObject = (JSONObject) obj;
      map = toMap(jsonObject);
    } catch (Exception e) {
      log.info(ExceptionUtils.getStackTrace(e));
    }

    return map;

  }

  private Map<String, Object> toMap(JSONObject object) throws Exception {

    Map<String, Object> map = new HashMap<String, Object>();

    for (Object key : object.keySet()) {

      Object value = object.get(key);

      if (value instanceof JSONObject) {

        value = toMap((JSONObject) value);

      }

      map.put(key + "", value);

    }

    return map;

  }
}
