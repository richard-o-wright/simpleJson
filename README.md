# simpleJson
Simple Java RestClient for all your Restful needs.

## Usage Example
```java
  Map<String, String> params = new HashMap<>();
  params.put("key", "Value");
  URL url = SimpleRestClient.buildUrl("https://example.com", "example/uri", params);
  SimpleRestClient restClient = new SimpleRestClient();
  MyResponse response = restClient.post(url, login.getAccessToken(), requestBody, MyResponse.class);
```
Get and post with response bodys are also supported.

How to add to your project as Maven dependency:
```
<dependency>
	<groupId>net.wrightnz.simple</groupId>
	<artifactId>simplejson</artifactId>
	<version>0.1.3</version>
</dependency>
```
