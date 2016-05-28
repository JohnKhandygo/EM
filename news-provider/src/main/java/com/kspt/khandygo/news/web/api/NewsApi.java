package com.kspt.khandygo.news.web.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/news")
@Produces(MediaType.APPLICATION_JSON)
public interface NewsApi {
  @Path("/")
  @GET
  NewsRepresentation getNewsForLastDay();
}
