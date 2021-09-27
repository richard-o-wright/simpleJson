package net.wrightnz.simple.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

class SimpleRestClientTest {

  @Test
  void construct() {
    SimpleRestClient client = new SimpleRestClient(
            30000, 
            30000, 
            false, 
            "http://localhost:8080", 
            "net.wrightnz.simple.simplejson"
    );
    Assertions.assertNotNull(client);
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