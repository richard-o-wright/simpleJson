package net.wrightnz.simple.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class SimpleRestClientTest {

  @Test
  void construct() {
    new SimpleRestClient(
            3000, 
            3000, 
            false, 
            "http://localhost:8080", 
            "net.wrightnz.simple.simplejson"
    );
  }

  @Test
  void buildUrl() throws IOException {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("foo", "bar");
    URL url = SimpleRestClient.buildUrl(
        "http://localhost:8888",
        "test-path",
        parameters
    );
    assertEquals(8888, url.getPort());
    assertEquals("localhost", url.getHost());
    assertEquals("http://localhost:8888/test-path?foo=bar", url.toString());
  }
}