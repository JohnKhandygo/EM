package com.kspt.khandygo.news.services;

import com.kspt.khandygo.news.core.NewsItem;
import com.kspt.khandygo.news.dao.NewsDAO;
import lombok.AllArgsConstructor;
import javax.inject.Inject;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class NewsService {

  private final NewsDAO newsDAO;

  public List<NewsItem> getNewsForLastDay() {
    return newsDAO.getNewsForLastDay();
  }
}
