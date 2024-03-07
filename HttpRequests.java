import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

//https://www.baeldung.com/java-httpclient-post
public class HttpRequests{
  String URL;
  String method;
  HttpClient client;
  HttpRequest request;

  HttpRequests(String URL, String method){
    this.client = HttpClient.newHttpClient();
    this.URL = URL;
    this.method = method;
    if(this.method.equalsIgnoreCase("GET")){
      this.request = HttpRequest.newBuilder()
      .uri(URI.create(this.URL))
      .GET()
      .build();
    }
    else if(this.method.equalsIgnoreCase("POST")){
      this.request = HttpRequest.newBuilder()
      .uri(URI.create(this.URL))
      .POST(HttpRequest.BodyPublishers.noBody())
      .build();
    }
    
  }

  HttpResponse<String> sendRequest() throws Exception{
    HttpResponse<String> response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
    return response;
  }
  HttpResponse<String> sendRequest(String data) throws Exception{
    if(this.method.equalsIgnoreCase("POST")){
      this.request = HttpRequest.newBuilder()
      .uri(URI.create(this.URL))
      .POST(HttpRequest.BodyPublishers.ofString(data))
      .build();

      HttpResponse<String> response = this.client.send(this.request, HttpResponse.BodyHandlers.ofString());
      return response;
    }
    else{
      return null;
    }
  }
}
