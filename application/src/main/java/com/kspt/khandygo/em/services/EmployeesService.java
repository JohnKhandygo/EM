package com.kspt.khandygo.em.services;

import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.dao.EmployeesDAO;
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
public class EmployeesService {

  @Inject
  private EmployeesDAO employeesDAO;

  @NonNull
  public List<Tuple2<Integer, ? extends Employee>> getAllUnderThePatronageOf(
      final @NonNull Employee manager) {
    return employeesDAO.getAllUnderThePatronageOf(manager);
  }
}
