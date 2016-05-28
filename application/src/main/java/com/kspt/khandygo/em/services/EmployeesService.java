package com.kspt.khandygo.em.services;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.EmployeesDAO;
import com.kspt.khandygo.em.utils.Tuple2;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class EmployeesService {

  private final EmployeesDAO employeesDAO;

  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final Employee manager) {
    return employeesDAO.getAllUnderThePatronageOf(manager);
  }
}
