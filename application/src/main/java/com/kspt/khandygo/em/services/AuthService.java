package com.kspt.khandygo.em.services;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class AuthService {

  private final Map<String, Tuple2<Integer, Employee>> authorizedUsers;

  private final AuthDAO authDAO;

  public Employee employeeBySession(final String session) {
    return authorizedUsers.get(session)._2;
  }

  public Integer employeeIdBySession(final String session) {
    return authorizedUsers.get(session)._1;
  }

  public String auth(final String login, final String password) {
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
