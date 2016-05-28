package com.kspt.khandygo.em.services;

import com.kspt.khandygo.news.web.api.NewsApi;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class NewsService {

  private final NewsApi externalApi;

  public List<NewsItem> getNewsForLastDay() {
    return externalApi.getNewsForLastDay().newsItems().stream()
        .map(nir -> new NewsItem(nir.origin(), nir.title(), nir.text()))
        .collect(toList());
  }

  @AllArgsConstructor
  @Accessors(fluent = true)
  @Getter
  public static class NewsItem {
    private final long origin;

    private final String title;

    private final String text;
  }
}
