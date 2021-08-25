package net.wrightnz.simple.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class SimpleRestClient {
  
  public static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";

  private static final String AUTH_HEADER = "Authorization";
  
  private final int connectionTimeout;
  private final int readTimeout;
  private final boolean isUnsafe;
  private final String referer;
  private final String userAgent;

  public SimpleRestClient(){
    this(30000, 30000, false, "", "");
  }

  /**
   * Create a new SimpleRestClient.
   * @param connectionTimeout
   * @param readTimeout
   * @param isUnsafe if true TLS (SSL) certificate verification is bypassed.
   * @param referer
   * @param userAgent
   */
  public SimpleRestClient(int connectionTimeout, int readTimeout, boolean isUnsafe, String referer, String userAgent){
    this.connectionTimeout = connectionTimeout;
    this.readTimeout = readTimeout;
    this.isUnsafe = isUnsafe;
    this.referer = referer;
    this.userAgent = userAgent;
  }

  /**
   * @param url         the url to get.
   * @param token       the session Cookie that identifies the current http session (if there is one,
   *                    can be null if there is not)
   * @param entityClass A class describing any response data
   * @param <T>         the type of the entityClass
   * @return an instance of the entityClass with any result sent back as a result of this post.
   * @throws IOException if there is a problem established or reading from the Http Connection.
   *                     maybe an HttpResponseException with more specific information about why the rest call failed.
   */
  public <T> T get(URL url, String token, Class<T> entityClass) throws IOException {
    Gson gson = new GsonBuilder().create();
    HttpURLConnection connection = getConnectionForGet(url, token);
    T result = getResponseBody(connection, gson, entityClass);
    return result;
  }

  /**
   * @param url         the url to post to
   * @param token       the session Cookie that identifies the current http session (if there is one,
   *                    can be null if there is not)
   * @param object      the data to be posted, can be null if there is no data to post.
   * @param entityClass A class describing any response data
   * @param <T>         the type of the entityClass
   * @return an instance of the entityClass with any result sent back as a result of this post.
   * @throws IOException if there is a problem established or reading from the Http Connection.
   *                     maybe an HttpResponseException with more specific information about why the rest call failed.
   */
  public <T> T post(URL url, String token, Object object, Class<T> entityClass) throws IOException {
    Gson gson = new GsonBuilder().create();
    HttpURLConnection connection = getConnectionForPost(url, token);
    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    try {
      writeRequestBody(writer, gson, object);
      writer.flush();
      T result = getResponseBody(connection, gson, entityClass);
      return result;
    } finally {
      writer.close();
    }
  }

  /**
   * @param url    the url to post to
   * @param token  the session Cookie that identifies the current http session (if there is one,
   *               can be null if there is not)
   * @param object the data to be posted, can be null if there is no data to post.
   * @return 
   * @throws IOException if there is a problem established or reading from the Http Connection.
   *                     maybe an HttpResponseException with more specific information about why the rest call failed.
   */
  public String post(URL url, String token, Object object) throws IOException {
    Gson gson = new GsonBuilder().create();
    HttpURLConnection connection = getConnectionForPost(url, token);
    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    try {
      writeRequestBody(writer, gson, object);
      writer.flush();
      return getResponseBody(connection, gson, String.class);
    } finally {
      writer.close();
    }
  }


  public static URL buildUrl(String host, String path, Map<String, String> parameters) throws IOException {
    StringBuilder urlBuilder = new StringBuilder(host);
    if (!host.endsWith("/") && !path.startsWith("/")) {
      urlBuilder.append("/");
    }
    urlBuilder.append(path);
    if (parameters != null && !parameters.isEmpty()) {
      urlBuilder.append("?");
      int i = 0;
      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        i++;
        urlBuilder.append(URLEncoder.encode(entry.getKey(), Charset.defaultCharset().name()));
        urlBuilder.append("=");
        urlBuilder.append(URLEncoder.encode(entry.getValue(), Charset.defaultCharset().name()));
        if (i < parameters.size()) {
          urlBuilder.append("&");
        }
      }
    }
    return new URL(urlBuilder.toString());
  }


  private void writeRequestBody(OutputStreamWriter writer, Gson gson, Object object) throws IOException {
    if (object != null) {
      String requestJson;
      if (object instanceof String) {
        requestJson = (String) object;
      } else {
        requestJson = gson.toJson(object);
      }
      writer.write(requestJson);
    } else {
      writer.write("\n");
    }
  }

  private <T> T getResponseBody(URLConnection connection, Gson gson, Class<T> entityClass) throws IOException {
    String json = getTextFromResponse(connection.getInputStream());
    return gson.fromJson(json, entityClass);
  }

  private HttpURLConnection getConnectionForGet(final URL url, final String token) throws IOException {
    HttpURLConnection connection = getConnection(url, token);
    connection.setRequestMethod("GET");
    return connection;
  }

  private HttpURLConnection getConnectionForPost(final URL url, final String token) throws IOException {
    HttpURLConnection connection = getConnection(url, token);
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    return connection;
  }

  private HttpURLConnection getConnection(final URL url, final String token) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    try {
      SSLContext sslContext = SSLContext.getInstance("SSL");
      if (isUnsafe) {
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      }
      if (connection instanceof HttpsURLConnection) {
        HttpsURLConnection sslConnection = (HttpsURLConnection) connection;
        if (isUnsafe) {
          sslConnection.setSSLSocketFactory(sslContext.getSocketFactory());
          sslConnection.setHostnameVerifier(allHostsValid);
        }
      }
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IOException("Problem with SSL connection set-up", e);
    }
    if (connection != null) {
      connection.setConnectTimeout(connectionTimeout);
      connection.setReadTimeout(readTimeout);
      Map<String, String> defaultHeaders = getDefaultHeaders();
      for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
      if (token != null) {
        if (token.contains("OAuth")) {
          connection.setRequestProperty(AUTH_HEADER, token);
        } else {
          connection.setRequestProperty(AUTH_HEADER, "Bearer " + token);
        }
      }
    }
    return connection;
  }

  /**
   * @return Map of default headers with referer and userAgent set.
   */
  private Map<String, String> getDefaultHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Accept", JSON_CONTENT_TYPE);
    headers.put("Content-Type", JSON_CONTENT_TYPE);
    headers.put("Referer", referer);
    headers.put("User-Agent", userAgent);
    return headers;
  }

  private TrustManager[] trustAllCerts = new TrustManager[]{
      new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
      }
  };

  private final HostnameVerifier allHostsValid = new HostnameVerifier() {
    @Override
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  };

  private String getTextFromResponse(InputStream response) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(response));
    StringBuilder out = new StringBuilder();
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        out.append(line);
      }
      return out.toString();
    } finally {
      reader.close();
    }
  }
}
