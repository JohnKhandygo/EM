package com.kspt.khandygo.em.services;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class EmployeesServiceTest {

  private EmployeesDAO employeesDAO;

  private EmployeesService employeesService;

  @Before
  public void setUp() {
    employeesDAO = mock(EmployeesDAO.class);
    employeesService = new EmployeesService(employeesDAO);
  }

  @Test
  public void whenAccessingPatronaged_delegateToDAO() {
    final Employee employee = mock(Employee.class);
    employeesService.getAllUnderThePatronageOf(employee);

    verify(employeesDAO, times(1)).getAllUnderThePatronageOf(eq(employee));
    verifyNoMoreInteractions(employeesDAO);
  }
}