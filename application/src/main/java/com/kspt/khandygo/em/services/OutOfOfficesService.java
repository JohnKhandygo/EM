package com.kspt.khandygo.em.services;

import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.OutOfOffice;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.dao.OutOfOfficesDAO;
import com.kspt.khandygo.em.utils.Tuple2;
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
public class OutOfOfficesService {

  @Inject
  private EmployeesDAO employeesDAO;

  @Inject
  private OutOfOfficesDAO outOfOfficesDAO;

  public int create(
      final @NonNull Employee requester,
      final long when,
      final long duration,
      final @NonNull String reason,
      final int employeeId) {
    final Employee employee = employeesDAO.get(employeeId);
    Preconditions.checkState(requester.equals(employee));
    final OutOfOffice outOfOffice = OutOfOffice.newOne(employee, when, duration, reason);
    return outOfOfficesDAO.save(outOfOffice);
  }

  public void cancel(final @NonNull Employee requester, final int id) {
    final OutOfOffice outOfOffice = outOfOfficesDAO.get(id);
    Preconditions.checkState(requester.equals(outOfOffice.employee()));
    final OutOfOffice cancelledVocation = outOfOffice.cancel();
    outOfOfficesDAO.update(id, cancelledVocation);
  }

  @NonNull
  public List<Tuple2<Integer, OutOfOffice>> approvedFor(final int employeeId) {
    return outOfOfficesDAO.approvedFor(employeeId);
  }
}
