package com.kspt.khandygo.em.services;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.OutOfOffice;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.dao.OutOfOfficesDAO;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class OutOfOfficesServiceTest {

  private EmployeesDAO employeesDAO;

  private OutOfOfficesDAO outOfOfficesDAO;

  private OutOfOfficesService service;

  @Before
  public void setUp() {
    employeesDAO = mock(EmployeesDAO.class);
    outOfOfficesDAO = mock(OutOfOfficesDAO.class);
    service = new OutOfOfficesService(employeesDAO, outOfOfficesDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenCreate_checkAccessRights() {
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(employeesDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.create(requester, 0, 0, "", 0);
  }

  @Test(expected = IllegalStateException.class)
  public void whenCreate_createdSaves() {
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(employeesDAO).get(0);

    final Employee requester = employee;
    service.create(requester, 0, 0, "", 0);

    verify(employeesDAO, times(1)).get(0);
    verify(outOfOfficesDAO, times(1)).save(any(OutOfOffice.class));
  }

  @Test(expected = IllegalStateException.class)
  public void whenCancel_checkAccessRights() {
    final OutOfOffice ooo = mock(OutOfOffice.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(ooo).employee();
    doReturn(ooo).when(outOfOfficesDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.cancel(requester, 0);
  }

  @Test
  public void whenCancel_cancelledSaves() {
    final OutOfOffice ooo = mock(OutOfOffice.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(ooo).employee();
    doReturn(ooo).when(outOfOfficesDAO).get(0);

    final Employee requester = employee;
    service.cancel(requester, 0);

    verify(outOfOfficesDAO, times(1)).get(0);
    verify(ooo, times(1)).cancel();
    verify(outOfOfficesDAO, times(1)).update(eq(0), any(OutOfOffice.class));
    verifyNoMoreInteractions(employeesDAO, outOfOfficesDAO);
  }

  @Test
  public void whenAccessApproved_delegateToDAO() {
    service.approvedFor(0);

    verify(outOfOfficesDAO, times(1)).approvedFor(0);
    verifyNoMoreInteractions(employeesDAO, outOfOfficesDAO);
  }
}