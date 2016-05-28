package com.kspt.khandygo.em.dao;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.utils.Tuple2;
import com.kspt.khandygo.persistence.Finder;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class AuthDAO {

  private final Finder finder;

  public Tuple2<Integer, Employee> get(final String login, final String password) {
    final UserEntity userEntity = finder.find(UserEntity.class).where()
        .eq("login", login)
        .and()
        .eq("password", hash(password))
        .unique();
    return new Tuple2<>(userEntity.id(), userEntity);
  }

  private String hash(final String string) {
    final MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(string.getBytes());
    byte bytes[] = md.digest();
    StringBuilder sb = new StringBuilder();
    for (final byte byteValue : bytes) {
      sb.append(Integer.toString((byteValue & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }
}
