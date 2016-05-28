package com.kspt.khandygo.em.services;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.OutOfOffice;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.dao.OutOfOfficesDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class OutOfOfficesService {

  private final EmployeesDAO employeesDAO;

  private final OutOfOfficesDAO outOfOfficesDAO;

  public int create(
      final Employee requester,
      final long when,
      final long duration,
      final String reason,
      final int employeeId) {
    final Employee employee = employeesDAO.get(employeeId);
    Preconditions.checkState(requester.equals(employee));
    final OutOfOffice outOfOffice = OutOfOffice.newOne(employee, when, duration, reason);
    return outOfOfficesDAO.save(outOfOffice);
  }

  public void update(
      final Employee requester,
      final int id,
      final long when,
      final long duration,
      final String reason) {
    final OutOfOffice outOfOffice = outOfOfficesDAO.get(id);
    Preconditions.checkState(requester.equals(outOfOffice.employee()));
    final OutOfOffice model = OutOfOffice.newOne(outOfOffice.employee(), when, duration, reason);
    outOfOfficesDAO.update(id, model);
  }

  public void cancel(final Employee requester, final int id) {
    final OutOfOffice outOfOffice = outOfOfficesDAO.get(id);
    Preconditions.checkState(requester.equals(outOfOffice.employee()));
    final OutOfOffice cancelledVocation = outOfOffice.cancel();
    outOfOfficesDAO.update(id, cancelledVocation);
  }

  public List<Tuple2<Integer, OutOfOffice>> approvedFor(final int employeeId) {
    return outOfOfficesDAO.approvedFor(employeeId);
  }
}
