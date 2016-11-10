package com.kspt.khandygo.em.services;

import com.google.common.base.VerifyException;
import com.google.common.collect.Maps;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.AuthDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import java.util.Map;

public class AuthServiceTest {

  private AuthDAO authDAO;

  private Map<String, Tuple2<Integer, Employee>> authorizedUsers;

  private AuthService authService;

  @Before
  public void setUp() {
    authDAO = mock(AuthDAO.class);
    authorizedUsers = Maps.newHashMap();
    authService = new AuthService(authorizedUsers, authDAO);
  }

  @Test(expected = NullPointerException.class)
  public void whenNoAuthorizedUsersForSession_NPEThrowsOnEmployeeAccess() {
    //WHEN
    authService.employeeBySession("");
  }

  @Test(expected = NullPointerException.class)
  public void whenNoAuthorizedUsersForSession_NPEThrowsOnEmployeeIdAccess() {
    //WHEN
    authService.employeeIdBySession("");
  }

  @Test(expected = VerifyException.class)
  public void whenAuthCredentialsNotRight_VEThrowsOnAuth() {
    //GIVEN
    doThrow(VerifyException.class).when(authDAO).get(anyString(), anyString());

    //WHEN
    authService.auth("", "");
  }

  @Test
  public void whenAuthCredentialsRight_AuthorizedUsersContainsCorrespondingEntry() {
    //GIVEN
    final Tuple2<Integer, Employee> fake = Tuple2.of(0, mock(Employee.class));
    doReturn(fake).when(authDAO).get(anyString(), anyString());

    //WHEN
    final String session = authService.auth("", "");

    //THEN
    assertThat(authorizedUsers).containsOnlyKeys(session);
    assertThat(authorizedUsers.get(session)).isEqualTo(fake);
  }

  @Test
  public void whenUserAuthenticated_employeeReturn() {
    //GIVEN
    final Tuple2<Integer, Employee> fake = Tuple2.of(0, mock(Employee.class));
    authorizedUsers.put("", fake);

    //WHEN
    final Employee response = authService.employeeBySession("");

    //THEN
    assertThat(response).isEqualTo(fake._2);
  }

  @Test
  public void whenUserAuthenticated_idReturn() {
    //GIVEN
    final Tuple2<Integer, Employee> fake = Tuple2.of(0, mock(Employee.class));
    authorizedUsers.put("", fake);

    //WHEN
    final Integer response = authService.employeeIdBySession("");

    //THEN
    assertThat(response).isEqualTo(0);
  }
}