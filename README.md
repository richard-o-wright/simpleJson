# simpleJson
Simple Java RestClient for all your Restful needs.

## Usage Example
```java
  Map<String, String> params = new HashMap<>();
  params.put("key", "Value");
  URL url = SimpleRestClient.buildUrl("https://example.com", "example/uri", params);
  SimpleRestClient restClient = new SimpleRestClient();
  restClient.post(url, login.getAccessToken(), requestBody);
```

Get and post with response bodys are also supported.




