package com.kspt.khandygo.em.dao;

import com.avaje.ebean.EbeanServer;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.utils.Tuple2;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class AuthDAO {

  private final EbeanServer ebean;

  public Tuple2<Integer, Employee> get(
      final @NonNull String login,
      final @NotNull String password) {
    final UserEntity userEntity = ebean.find(UserEntity.class).where()
        .eq("login", login)
        .and()
        .eq("password", hash(password))
        .findUnique();
    return Tuple2.of(userEntity.id(), userEntity);
  }

  private String hash(final @NonNull String string) {
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
