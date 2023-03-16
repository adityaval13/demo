package com.example.demo.utils;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class MDCUtil {
  // Put a context value as identified by key
  // into the current thread's context map.
  public void put(String key, String val) {
    MDC.put(key, val);
  }

  // Get the context identified by the key parameter.
  public String get(String key) {
    return MDC.get(key).toString();
  }

  // Remove the context identified by the key parameter.
  public void remove(String key) {
    MDC.remove(key);
  }

  // Clear all entries in the MDC.
  public void clear() {
    MDC.clear();
  }

  public void putId() {
    String unique_id = UUID.randomUUID().toString().replace("-", "");
    put("id", unique_id);
  }

  public void putIdSendiri(String id) {
    put("id", id);
  }
}