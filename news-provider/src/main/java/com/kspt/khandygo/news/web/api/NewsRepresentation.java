package com.kspt.khandygo.news.web.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeName(".news")
//@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public class NewsRepresentation {
  @JsonProperty("news_items")
  private final List<NewsItemRepresentation> newsItems;

  @JsonCreator
  public NewsRepresentation(
      final @JsonProperty("news_items") List<NewsItemRepresentation> newsItems) {
    this.newsItems = newsItems;
  }
}
