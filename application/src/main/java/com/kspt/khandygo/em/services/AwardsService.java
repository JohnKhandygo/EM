package com.kspt.khandygo.em.services;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Award;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.AwardsDAO;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Stateless
@LocalBean
public class AwardsService {

  @Inject
  private EmployeesDAO employeesDAO;

  @Inject
  private AwardsDAO awardsDAO;

  @NonNull
  public List<Tuple2<Integer, Award>> approvedFor(final int employeeId) {
    return awardsDAO.approvedFor(employeeId);
  }

  @NonNull
  public List<Tuple2<Integer, Award>> pendingInboxFor(final int employeeId) {
    return employeesDAO.getAllMasteredBy(employeeId).stream()
        .map(t2 -> t2._1)
        .map(awardsDAO::pendingFor)
        .flatMap(List::stream)
        .sorted(comparingLong(t2 -> t2._2.when()))
        .collect(toList());
  }

  @NonNull
  public List<Tuple2<Integer, Award>> pendingOutboxFor(final int employeeId) {
    return employeesDAO.getAllUnderThePatronageOf(employeeId).stream()
        .map(t2 -> t2._1)
        .map(awardsDAO::pendingFor)
        .flatMap(List::stream)
        .sorted(comparingLong(t2 -> t2._2.when()))
        .collect(toList());
  }

  public int propose(
      final @NonNull Employee requester,
      final long when,
      final long duration,
      final int employeeId) {
    final Employee employee = employeesDAO.get(employeeId);
    Preconditions.checkState(requester.equals(employee.manager()));
    final Award award = Award.newOne(employee, when, duration);
    return awardsDAO.save(award);
  }

  public void approve(final @NonNull Employee requester, final int id) {
    final Award award = awardsDAO.get(id);
    Preconditions.checkState(requester.equals(award.employee().paymaster()));
    awardsDAO.update(id, award.approve());
  }

  public void reject(final @NonNull Employee requester, final int id) {
    final Award award = awardsDAO.get(id);
    Preconditions.checkState(requester.equals(award.employee().paymaster()));
    awardsDAO.update(id, award.reject());
  }

  public void cancel(final @NonNull Employee requester, final int id) {
    final Award award = awardsDAO.get(id);
    Preconditions.checkState(requester.equals(award.employee().manager()));
    awardsDAO.update(id, award.cancel());
  }
}
