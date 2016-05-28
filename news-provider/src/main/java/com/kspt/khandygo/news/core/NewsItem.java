package com.kspt.khandygo.news.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public class NewsItem {

  private final long origin;

  private final String title;

  private final String text;
}
