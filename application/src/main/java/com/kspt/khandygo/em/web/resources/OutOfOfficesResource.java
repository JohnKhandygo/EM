package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.OutOfOffice;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.em.services.OutOfOfficesService;
import com.kspt.khandygo.em.utils.Tuple2;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/out_of_offices")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class OutOfOfficesResource {

  private final AuthService authService;

  private final OutOfOfficesService outOfOfficesService;

  @Path("/approved")
  @GET
  public List<OutOfOfficeRepresentation> get(final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, OutOfOffice>> awards = outOfOfficesService.approvedFor(requesterId);
    return awards.stream()
        .map(t2 ->
            new OutOfOfficeRepresentation(t2._1, t2._2.when(), t2._2.duration(), t2._2.reason()))
        .collect(toList());
  }

  @Path("/create")
  @POST
  public OutOfOfficeCreated create(
      final @HeaderParam("session_id") String session,
      final @FormParam("when") long when,
      final @FormParam("duration") long duration,
      final @FormParam("reason") String reason) {
    final Employee requester = authService.employeeBySession(session);
    final int employeeId = authService.employeeIdBySession(session);
    final int id = outOfOfficesService.create(requester, when, duration, reason, employeeId);
    return new OutOfOfficeCreated(id);
  }

  @Path("/{out_of_office_id}/cancel")
  @DELETE
  public OutOfOfficeCancelled cancel(
      final @HeaderParam("session_id") String session,
      final @PathParam("out_of_office_id") int outOfOfficeId) {
    final Employee requester = authService.employeeBySession(session);
    outOfOfficesService.cancel(requester, outOfOfficeId);
    return new OutOfOfficeCancelled();
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".out_of_office")
  @AllArgsConstructor
  private static class OutOfOfficeRepresentation {
    private final int id;

    private final long when;

    private final long duration;

    private final String reason;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".out_of_office_created")
  @AllArgsConstructor
  private static class OutOfOfficeCreated {
    private final int id;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".out_of_office_updated")
  private static class OutOfOfficeUpdated {}

  @ResourceRepresentationWithType
  @JsonTypeName(".out_of_office_cancelled")
  private static class OutOfOfficeCancelled {}
}
