package com.kspt.khandygo.em.services;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.Vocation;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.dao.VocationsDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class VocationsService {

  private final EmployeesDAO employeesDAO;

  private final VocationsDAO vocationsDAO;

  public int propose(
      final Employee requester,
      final long when,
      final long duration,
      final int employeeId) {
    final Employee employee = employeesDAO.get(employeeId);
    Preconditions.checkState(requester.equals(employee));
    final Vocation vocation = Vocation.newOne(employee, when, duration);
    return vocationsDAO.save(vocation);
  }

  public void approve(final Employee requester, final int id) {
    final Vocation vocation = vocationsDAO.get(id);
    Preconditions.checkState(requester.equals(vocation.employee().manager()));
    final Vocation approvedVocation = vocation.approve();
    vocationsDAO.update(id, approvedVocation);
  }

  public void reject(final Employee requester, final int id) {
    final Vocation vocation = vocationsDAO.get(id);
    Preconditions.checkState(requester.equals(vocation.employee().manager()));
    final Vocation rejectedVocation = vocation.reject();
    vocationsDAO.update(id, rejectedVocation);
  }

  public void cancel(final Employee requester, final int id) {
    final Vocation vocation = vocationsDAO.get(id);
    Preconditions.checkState(requester.equals(vocation.employee()));
    final Vocation cancelledVocation = vocation.cancel();
    vocationsDAO.update(id, cancelledVocation);
  }

  public List<Tuple2<Integer, Vocation>> approvedFor(final int employeeId) {
    return vocationsDAO.approvedFor(employeeId);
  }

  public List<Tuple2<Integer, Vocation>> pendingInboxFor(final int employeeId) {
    return employeesDAO.getAllMasteredBy(employeeId).stream()
        .map(t2 -> t2._1)
        .map(vocationsDAO::pendingFor)
        .flatMap(List::stream)
        .sorted(comparingLong(t2 -> t2._2.when()))
        .collect(toList());
  }

  public List<Tuple2<Integer, Vocation>> pendingOutboxFor(final int employeeId) {
    return vocationsDAO.pendingFor(employeeId);
  }
}