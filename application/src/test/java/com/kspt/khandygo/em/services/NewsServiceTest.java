/*
package com.kspt.khandygo.em.services;

import com.kspt.khandygo.news.web.api.NewsApi;
import com.kspt.khandygo.news.web.api.NewsRepresentation;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class NewsServiceTest {

  private NewsApi externalApi;

  private NewsService service;

  @Before
  public void setUp() {
    externalApi = mock(NewsApi.class);
    service = new NewsService(externalApi);
  }

  @Test
  public void whenAccessingNews_delegateToExternalService() {
    //GIVEN
    doReturn(mock(NewsRepresentation.class)).when(externalApi).getNewsForLastDay();

    //WHEN
    service.getNewsForLastDay();

    //THEN
    verify(externalApi, times(1)).getNewsForLastDay();
  }
}*/
