package com.kspt.khandygo.news;

import com.google.common.io.Resources;
import com.kspt.khandygo.news.web.NewsProviderApp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewsProviderAppRunner {
  public static void main(String[] args)
  throws Exception {
    try {
      final String configFile = Resources.getResource("app.yml").getFile();
      new NewsProviderApp().run("server", configFile);
    } catch (Exception e) {
      log.error("Error occurred", e);
      throw new RuntimeException(e);
    }
  }
}