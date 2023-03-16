package com.example.demo.utils;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

  @Autowired
  UtilHelper util;

  // secret key untuk menghasilkan dan memvalidasi JWT
  private static final String SECRET_KEY = "aditya";

  // waktu kadaluarsa JWT dalam milidetik (1 jam)
  // private static final long EXPIRATION_TIME = 3600000;
  private static final long EXPIRATION_TIME = 1000;

  /**
   * Menghasilkan JWT dengan menggunakan HS256 metadata dan secret key yang telah
   * diberikan.
   * 
   * @param subject subjek (sub) dari JWT.
   * @param claims  klaim (claim) tambahan yang ingin dimasukkan ke dalam JWT.
   * @return JWT sebagai string.
   */
  public String generateJwt(String subject, Claims claims) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
    return Jwts.builder()
        .setSubject(subject)
        .addClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
        .compact();
  }

  /**
   * Memvalidasi JWT dengan menggunakan HS256 metadata dan secret key yang telah
   * diberikan.
   * 
   * @param jwt JWT yang ingin divalidasi.
   * @return true jika JWT valid, false jika JWT tidak valid.
   */
  public boolean validateJwt(String jwt) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwt);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Membaca subjek dan klaim dari JWT yang telah diberikan.
   * 
   * @param jwt JWT yang ingin dibaca.
   * @return objek Jws yang berisi subjek dan klaim dari JWT.
   */
  private Jws<Claims> readJwt(String jwt) {
    return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwt);
  }

  public Map<String, Object> getBodyFromJwt(String msg) {
    try {
      Jws<Claims> jws = readJwt(msg);
      Claims claims = jws.getBody();
      String subject = claims.getSubject();
      String name = claims.get("name", java.lang.String.class);
      Map<String, Object> data = util.jsonToMap(subject);

      System.out.println("name = " + name);
      System.out.println("expiration = " + claims.getExpiration());
      System.out.println("data = " + data);

      return data;
    } catch (Exception e) {
      log.info(ExceptionUtils.getStackTrace(e));
    }

    return null;
  }
}
