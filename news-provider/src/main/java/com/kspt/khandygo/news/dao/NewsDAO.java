package com.kspt.khandygo.news.dao;

import com.kspt.khandygo.news.core.NewsItem;
import com.kspt.khandygo.persistence.Gateway;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(onConstructor = @__({@Inject}))
@Singleton
public class NewsDAO {

  private final static long MS_IN_DAY = TimeUnit.DAYS.toMillis(1);

  private final Gateway gateway;

  public List<NewsItem> getNewsForLastDay() {
    return gateway.find(NewsItemEntry.class).where()
        .moreThen("origin", System.currentTimeMillis() - MS_IN_DAY)
        .desc("origin")
        .list()
        .stream()
        .map(NewsItemEntry::toNewsItem)
        .collect(toList());
  }

  @Entity
  @Table(name = "news_items")
  @AllArgsConstructor
  @Accessors(fluent = true)
  @Getter
  private static class NewsItemEntry {
    @Id
    private final Integer id;

    private final Long origin;

    private final String title;

    private final String text;

    NewsItem toNewsItem() {
      return new NewsItem(origin, title, text);
    }
  }
}
