package com.kspt.khandygo.em.services;

import static com.google.common.collect.Lists.newArrayList;
import com.kspt.khandygo.em.core.Award;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.AwardsDAO;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class AwardsServiceTest {

  private EmployeesDAO employeesDAO;

  private AwardsDAO awardsDAO;

  private AwardsService awardsService;

  @Before
  public void setUp() {
    employeesDAO = mock(EmployeesDAO.class);
    awardsDAO = mock(AwardsDAO.class);
    awardsService = new AwardsService(employeesDAO, awardsDAO);
  }

  @Test
  public void whenAccessApprovedForUser_delegateToDAO() {
    awardsService.approvedFor(0);
    verify(awardsDAO, times(1)).approvedFor(0);
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }

  @Test
  public void whenAccessPendingInbox_pendingForIsAccessed() {
    doReturn(
        newArrayList(
            new Tuple2<>(0, mock(Employee.class)),
            new Tuple2<>(1, mock(Employee.class))))
        .when(employeesDAO).getAllMasteredBy(anyInt());
    awardsService.pendingInboxFor(0);
    verify(employeesDAO, times(1)).getAllMasteredBy(0);
    verify(awardsDAO, times(1)).pendingFor(0);
    verify(awardsDAO, times(1)).pendingFor(1);
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }

  @Test
  public void whenAccessPendingOutbox_pendingForIsAccessed() {
    doReturn(
        newArrayList(
            new Tuple2<>(0, mock(Employee.class)),
            new Tuple2<>(1, mock(Employee.class))))
        .when(employeesDAO).getAllUnderThePatronageOf(anyInt());
    awardsService.pendingOutboxFor(0);
    verify(employeesDAO, times(1)).getAllUnderThePatronageOf(0);
    verify(awardsDAO, times(1)).pendingFor(0);
    verify(awardsDAO, times(1)).pendingFor(1);
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenProposeAwards_checkAccessRights() {
    final Employee manager = mock(Employee.class);
    final Employee employee = mock(Employee.class);
    doReturn(mock(Employee.class)).when(employee).manager();
    doReturn(employee).when(employeesDAO).get(anyInt());

    awardsService.propose(manager, 0, 0, 0);
  }

  @Test
  public void whenProposeAwards_awardsSaves() {
    final Employee manager = mock(Employee.class);
    final Employee employee = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(employee).when(employeesDAO).get(anyInt());

    awardsService.propose(manager, 0, 0, 0);
    verify(awardsDAO, times(1)).save(any());
  }

  @Test(expected = IllegalStateException.class)
  public void whenApprove_checkAccessRights() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee paymaster = mock(Employee.class);
    doReturn(paymaster).when(employee).paymaster();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = mock(Employee.class);
    awardsService.approve(requester, 0);
  }

  @Test
  public void whenApprove_approvedSaves() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee paymaster = mock(Employee.class);
    doReturn(paymaster).when(employee).paymaster();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = paymaster;
    awardsService.approve(requester, 0);

    verify(awardsDAO, times(1)).get(0);
    verify(award, times(1)).approve();
    verify(awardsDAO, times(1)).update(eq(0), any(Award.class));
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenReject_checkAccessRights() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee paymaster = mock(Employee.class);
    doReturn(paymaster).when(employee).paymaster();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = mock(Employee.class);
    awardsService.reject(requester, 0);
  }

  @Test
  public void whenReject_rejectedSaves() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee paymaster = mock(Employee.class);
    doReturn(paymaster).when(employee).paymaster();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = paymaster;
    awardsService.reject(requester, 0);

    verify(awardsDAO, times(1)).get(0);
    verify(award, times(1)).reject();
    verify(awardsDAO, times(1)).update(eq(0), any(Award.class));
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }

  @Test(expected = IllegalStateException.class)
  public void whenCancel_checkAccessRights() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = mock(Employee.class);
    awardsService.cancel(requester, 0);
  }

  @Test
  public void whenCancel_cancelledSaves() {
    final Award award = mock(Award.class);
    final Employee employee = mock(Employee.class);
    doReturn(employee).when(award).employee();
    final Employee manager = mock(Employee.class);
    doReturn(manager).when(employee).manager();
    doReturn(award).when(awardsDAO).get(0);

    final Employee requester = manager;
    awardsService.cancel(requester, 0);

    verify(awardsDAO, times(1)).get(0);
    verify(award, times(1)).cancel();
    verify(awardsDAO, times(1)).update(eq(0), any(Award.class));
    verifyNoMoreInteractions(employeesDAO, awardsDAO);
  }
}