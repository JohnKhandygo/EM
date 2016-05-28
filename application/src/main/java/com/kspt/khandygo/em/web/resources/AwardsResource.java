package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kspt.khandygo.em.core.Award;
import com.kspt.khandygo.em.core.Employee;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.em.services.AwardsService;
import com.kspt.khandygo.em.utils.Tuple2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/awards")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Api(value = "/awards")
public class AwardsResource {

  private final AuthService authService;

  private final AwardsService awardsService;

  @Path("/approved")
  @GET
  @ApiOperation(value = "get approved awards for employee.")
  public List<AwardRepresentation> getApproved(final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Award>> awards = awardsService.approvedFor(requesterId);
    return represent(awards);
  }

  private List<AwardRepresentation> represent(final List<Tuple2<Integer, Award>> awards) {
    return awards.stream()
        .map(t2 -> new AwardRepresentation(t2._1, t2._2.when(), t2._2.amount()))
        .collect(toList());
  }

  @Path("/pending/inbox")
  @GET
  @ApiOperation(value = "get pending awards awaiting for employee decision.")
  public List<AwardRepresentation> getPendingInbox(
      final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Award>> awards = awardsService.pendingInboxFor(requesterId);
    return represent(awards);
  }

  @Path("/pending/outbox")
  @GET
  @ApiOperation(value = "get pending awards proposed by employee.")
  public List<AwardRepresentation> getPendingOutbox(
      final @HeaderParam("session_id") String session) {
    final int requesterId = authService.employeeIdBySession(session);
    final List<Tuple2<Integer, Award>> awards = awardsService.pendingOutboxFor(requesterId);
    return represent(awards);
  }

  @Path("/propose")
  @POST
  @ApiOperation(value = "propose award.")
  public AwardProposed propose(
      final @HeaderParam("session_id") String session,
      final @FormParam("when") long when,
      final @FormParam("amount") long amount,
      final @FormParam("employee_id") int employeeId) {
    final Employee requester = authService.employeeBySession(session);
    final int id = awardsService.propose(requester, when, amount, employeeId);
    return new AwardProposed(id);
  }

  @Path("/{award_id}/approve")
  @POST
  @ApiOperation(value = "approve award.")
  public AwardApproved approve(
      final @HeaderParam("session_id") String session,
      final @PathParam("award_id") int awardId) {
    final Employee requester = authService.employeeBySession(session);
    awardsService.approve(requester, awardId);
    return new AwardApproved();
  }

  @Path("/{award_id}/reject")
  @POST
  @ApiOperation(value = "reject award.")
  public AwardRejected reject(
      final @HeaderParam("session_id") String session,
      final @PathParam("award_id") int awardId) {
    final Employee requester = authService.employeeBySession(session);
    awardsService.reject(requester, awardId);
    return new AwardRejected();
  }

  @Path("/{award_id}/cancel")
  @POST
  @ApiOperation(value = "cancel award.")
  public AwardCancelled cancel(
      final @HeaderParam("session_id") String session,
      final @PathParam("award_id") int awardId) {
    final Employee requester = authService.employeeBySession(session);
    awardsService.cancel(requester, awardId);
    return new AwardCancelled();
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".award")
  @AllArgsConstructor
  private static class AwardRepresentation {
    private final int id;

    private final long when;

    private final long amount;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".award_proposed")
  @AllArgsConstructor
  private static class AwardProposed {
    private final int id;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".award_approved")
  private static class AwardApproved {}

  @ResourceRepresentationWithType
  @JsonTypeName(".award_rejected")
  private static class AwardRejected {}

  @ResourceRepresentationWithType
  @JsonTypeName(".award_cancelled")
  private static class AwardCancelled {}
}
