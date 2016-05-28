package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.kspt.khandygo.em.services.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Api(value = "/auth")
public class AuthResource {

  private final AuthService authService;

  @Path("/")
  @POST
  @ApiOperation(value = "authorize using login and password.")
  public UserAuthorized authorize(
      final @FormParam("login") String login,
      final @FormParam("password") String password) {
    final String session = authService.auth(login, password);
    return new UserAuthorized(session);
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".user_authorized")
  @AllArgsConstructor
  private static class UserAuthorized {
    private final String session;
  }
}
