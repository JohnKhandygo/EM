package com.kspt.khandygo.em.services;

import static com.google.common.collect.Lists.newArrayList;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.Vocation;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.dao.VocationsDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import java.util.concurrent.TimeUnit;

public class VocationsServiceTest {

  private EmployeesDAO employeesDAO;

  private VocationsDAO vocationsDAO;

  private VocationsService service;

  @Before
  public void setUp() {
    employeesDAO = mock(EmployeesDAO.class);
    vocationsDAO = mock(VocationsDAO.class);
    service = new VocationsService(employeesDAO, vocationsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenPropose_checkAccessRights() {
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(employeesDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.propose(requester, 0, 0, 0);
  }

  @Test
  public void whenPropose_proposedSaves() {
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(employeesDAO).get(0);

    final Employee requester = employee;
    final long when = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8);
    final long duration = TimeUnit.DAYS.toMillis(8);
    service.propose(requester, when, duration, 0);

    verify(employeesDAO, times(1)).get(0);
    verify(vocationsDAO, times(1)).save(any(Vocation.class));
  }

  @Test(expected = IllegalStateException.class)
  public void whenApprove_checkAccessRights() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.approve(requester, 0);
  }

  @Test
  public void whenApprove_approvedSaves() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = manager;
    service.approve(requester, 0);

    verify(vocationsDAO, times(1)).get(0);
    verify(vocation, times(1)).approve();
    verify(vocationsDAO, times(1)).update(eq(0), any(Vocation.class));
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenReject_checkAccessRights() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.reject(requester, 0);
  }

  @Test
  public void whenReject_rejectedSaves() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = manager;
    service.reject(requester, 0);

    verify(vocationsDAO, times(1)).get(0);
    verify(vocation, times(1)).reject();
    verify(vocationsDAO, times(1)).update(eq(0), any(Vocation.class));
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenCancel_checkAccessRights() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = mock(Employee.class);
    service.cancel(requester, 0);
  }

  @Test
  public void whenCancel_cancelledSaves() {
    final Vocation vocation = mock(Vocation.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(vocation).employee();
    doReturn(vocation).when(vocationsDAO).get(0);

    final Employee requester = employee;
    service.cancel(requester, 0);

    verify(vocationsDAO, times(1)).get(0);
    verify(vocation, times(1)).cancel();
    verify(vocationsDAO, times(1)).update(eq(0), any(Vocation.class));
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }

  @Test
  public void whenAccessApprovedForUser_delegateToDAO() {
    service.approvedFor(0);
    verify(vocationsDAO, times(1)).approvedFor(0);
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }

  @Test
  public void whenAccessPendingInbox_pendingForIsAccessed() {
    doReturn(
        newArrayList(
            new Tuple2<>(0, mock(Employee.class)),
            new Tuple2<>(1, mock(Employee.class))))
        .when(employeesDAO).getAllMasteredBy(0);
    service.pendingInboxFor(0);
    verify(employeesDAO, times(1)).getAllMasteredBy(0);
    verify(vocationsDAO, times(1)).pendingFor(0);
    verify(vocationsDAO, times(1)).pendingFor(1);
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }

  @Test
  public void whenAccessPendingOutbox_delegateToDAO() {
    doReturn(
        newArrayList(
            new Tuple2<>(0, mock(Employee.class)),
            new Tuple2<>(1, mock(Employee.class))))
        .when(employeesDAO).getAllUnderThePatronageOf(0);
    service.pendingOutboxFor(0);
    verify(vocationsDAO, times(1)).pendingFor(0);
    verifyNoMoreInteractions(employeesDAO, vocationsDAO);
  }
}