package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.core.Vocation;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.em.services.VocationsService;
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

@Path("/vocations")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class VocationsResource {

  private final AuthService authService;

  private final VocationsService vocationsService;

  @Path("/approved")
  @GET
  public List<VocationRepresentation> get(final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Vocation>> awards = vocationsService.approvedFor(requesterId);
    return represent(awards);
  }

  private List<VocationRepresentation> represent(final List<Tuple2<Integer, Vocation>> awards) {
    return awards.stream()
        .map(t2 -> new VocationRepresentation(t2._1, t2._2.when(), t2._2.duration()))
        .collect(toList());
  }

  @Path("/pending/inbox")
  @GET
  public List<VocationRepresentation> getPendingInbox(
      final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Vocation>> awards = vocationsService.pendingInboxFor(requesterId);
    return represent(awards);
  }

  @Path("/pending/outbox")
  @GET
  public List<VocationRepresentation> getPendingOutbox(
      final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Vocation>> awards = vocationsService.pendingOutboxFor(requesterId);
    return represent(awards);
  }

  @Path("/propose")
  @POST
  public VocationProposed propose(
      final @HeaderParam("session_id") String session,
      final @FormParam("when") long when,
      final @FormParam("duration") long duration) {
    final Employee requester = authService.employeeBySession(session);
    final int employeeId = authService.employeeIdBySession(session);
    final int id = vocationsService.propose(requester, when, duration, employeeId);
    return new VocationProposed(id);
  }

  @Path("/{vocation_id}/approve")
  @POST
  public VocationApproved approve(
      final @HeaderParam("session_id") String session,
      final @PathParam("vocation_id") int vocationId) {
    final Employee requester = authService.employeeBySession(session);
    vocationsService.approve(requester, vocationId);
    return new VocationApproved();
  }

  @Path("/{vocation_id}/reject")
  @POST
  public VocationRejected reject(
      final @HeaderParam("session_id") String session,
      final @PathParam("vocation_id") int vocationId) {
    final Employee requester = authService.employeeBySession(session);
    vocationsService.reject(requester, vocationId);
    return new VocationRejected();
  }

  @Path("/{vocation_id}/cancel")
  @DELETE
  public VocationCancelled cancel(
      final @HeaderParam("session_id") String session,
      final @PathParam("vocation_id") int vocationId) {
    final Employee requester = authService.employeeBySession(session);
    vocationsService.cancel(requester, vocationId);
    return new VocationCancelled();
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".vocation")
  @AllArgsConstructor
  private static class VocationRepresentation {
    private final int id;

    private final long when;

    private final long duration;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".vocation_proposed")
  @AllArgsConstructor
  private static class VocationProposed {
    private final int id;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".vocation_approved")
  private static class VocationApproved {}

  @ResourceRepresentationWithType
  @JsonTypeName(".vocation_rejected")
  private static class VocationRejected {}

  @ResourceRepresentationWithType
  @JsonTypeName(".vocation_cancelled")
  private static class VocationCancelled {}
}
