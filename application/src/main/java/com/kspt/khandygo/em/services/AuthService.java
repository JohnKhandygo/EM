package com.kspt.khandygo.em.services;

import com.google.common.collect.Maps;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.Objects.requireNonNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


@AllArgsConstructor
@Stateful
@LocalBean
@Singleton
public class AuthService {

  private final Map<String, Tuple2<Integer, Employee>> authorizedUsers;

  @Inject
  private AuthDAO authDAO;

  public AuthService() {
    this.authorizedUsers = Maps.newConcurrentMap();
  }

  @NonNull
  public Employee employeeBySession(final @NonNull String session) {
    return requireNonNull(authorizedUsers.get(session)._2);
  }

  public int employeeIdBySession(final @NonNull String session) {
    return requireNonNull(authorizedUsers.get(session)._1);
  }

  @NonNull
  public String auth(final @NonNull String login, final @NonNull String password) {
    final Tuple2<Integer, Employee> userWithId = authDAO.get(login, password);
    if (authorizedUsers.containsValue(userWithId))
      return authorizedUsers.entrySet().stream()
          .filter(e -> e.getValue().equals(userWithId))
          .map(Entry::getKey)
          .findFirst()
          .get();
    final String session = UUID.randomUUID().toString();
    authorizedUsers.put(session, userWithId);
    return session;
  }
}
