/*
package com.kspt.khandygo.em.web.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import com.kspt.khandygo.em.services.AuthService;
import com.kspt.khandygo.em.services.NewsService;
import io.swagger.annotations.Api;
import static java.util.stream.Collectors.*;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
@Api(value = "/news")
public class NewsResource {

  private final NewsService service;

  private final AuthService authService;

  @Path("/")
  @GET
  public NewsRepresentation getNewsForLastDay(
      final @HeaderParam("session_id") String session) {
    Preconditions.checkNotNull(authService.employeeBySession(session));
    return service.getNewsForLastDay().stream()
        .map(ni -> new NewsItemRepresentation(ni.origin(), ni.title(), ni.text()))
        .collect(collectingAndThen(toList(), NewsRepresentation::new));
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".news")
  @AllArgsConstructor
  private static class NewsRepresentation {
    @JsonProperty("news_items")
    private final List<NewsItemRepresentation> newsItems;
  }

  @ResourceRepresentationWithType
  @JsonTypeName(".news_item")
  @AllArgsConstructor
  private static class NewsItemRepresentation {
    private final long origin;

    private final String title;

    private final String text;
  }
}
*/
