package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.em.services.EmployeesService;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class EmployeesResource {

  private final AuthService authService;

  private final EmployeesService employeesService;

  @Path("/manager")
  @GET
  public EmployeeRepresentation getManager(
      final @HeaderParam("session_id") String session) {
    final Employee manager = authService.employeeBySession(session).manager();
    return Optional.ofNullable(manager)
        .map(Employee::name)
        .map(EmployeeRepresentation::forName)
        .orElseGet(EmployeeRepresentation::empty);
  }

  @Path("/paymaster")
  @GET
  public EmployeeRepresentation getPaymaster(
      final @HeaderParam("session_id") String session) {
    final Employee paymaster = authService.employeeBySession(session).paymaster();
    return Optional.ofNullable(paymaster)
        .map(Employee::name)
        .map(EmployeeRepresentation::forName)
        .orElseGet(EmployeeRepresentation::empty);
  }

  @Path("/teammates")
  @GET
  public List<EmployeeRepresentation> getTeammates(
      final @HeaderParam("session_id") String session) {
    final Employee employee = authService.employeeBySession(session);
    final Employee manager = employee.manager();
    return employeesService.getAllUnderThePatronageOf(manager)
        .stream()
        .<Employee>map(t2 -> t2._2)
        .filter(e -> !e.equals(employee))
        .map(Employee::name)
        .map(EmployeeRepresentation::forName)
        .collect(toList());
  }

  @Path("/patronaged")
  @GET
  public List<EmployeeRepresentation> getPatronaged(
      final @HeaderParam("session_id") String session) {
    final Employee employee = authService.employeeBySession(session);
    return employeesService.getAllUnderThePatronageOf(employee)
        .stream()
        .map(t2 -> EmployeeRepresentation.forNameAndId(t2._2.name(), t2._1))
        .collect(toList());
  }

  private static class EmployeeRepresentation {
    static EmployeeRepresentation forName(final String name) {
      return new ExistedEmployeeRepresentation(name);
    }

    static EmployeeRepresentation forNameAndId(final String name, final int id) {
      return new EmployeeWithIdRepresentation(name, id);
    }

    static EmployeeRepresentation empty() {
      return new NullEmployeeRepresentation();
    }

    @ResourceRepresentationWithType
    @JsonTypeName(".employee")
    @AllArgsConstructor
    private static class ExistedEmployeeRepresentation extends EmployeeRepresentation {
      private final String name;
    }

    @ResourceRepresentationWithType
    @JsonTypeName(".employee_with_id")
    private static class EmployeeWithIdRepresentation extends ExistedEmployeeRepresentation {
      private final int id;

      EmployeeWithIdRepresentation(final String name, final int id) {
        super(name);
        this.id = id;
      }
    }

    @ResourceRepresentationWithType
    @JsonTypeName(".null")
    @AllArgsConstructor
    private static class NullEmployeeRepresentation extends EmployeeRepresentation {}
  }
}
