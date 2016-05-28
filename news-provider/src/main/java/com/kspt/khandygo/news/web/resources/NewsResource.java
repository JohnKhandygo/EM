package com.kspt.khandygo.news.web.resources;

import com.kspt.khandygo.news.services.NewsService;
import com.kspt.khandygo.news.web.api.NewsApi;
import com.kspt.khandygo.news.web.api.NewsItemRepresentation;
import com.kspt.khandygo.news.web.api.NewsRepresentation;
import static java.util.stream.Collectors.*;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/news")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class NewsResource implements NewsApi {

  private final NewsService service;

  public NewsRepresentation getNewsForLastDay() {
    return service.getNewsForLastDay().stream()
        .map(ni -> new NewsItemRepresentation(ni.origin(), ni.title(), ni.text()))
        .collect(collectingAndThen(toList(), NewsRepresentation::new));
  }
}
