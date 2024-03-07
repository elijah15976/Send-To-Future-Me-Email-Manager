import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpContext;
//import com.sun.corba.se.impl.ior.IORImpl;
//import com.sun.net.httpserver.Headers;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URI;
//import java.sql.*;
/*
  EP - Feb 7, 2023, 
  Overloading Routehandler to reflect the 
  overloading of the runSQL function in Database class
  that accepts a return format.  
*/
public class RouteHandler implements HttpHandler {
    public String route;
    private String response;
    private String file;
    private String contentType;
    private Database db;
    private String sql;
    private static Logger lg = new Logger();

    public RouteHandler(String response, String route){
      this.response = response;
      this.contentType = "text/plain";
      this.route = route;
    }
    public RouteHandler(String file, String type, String route){
      this.file = file;
      this.contentType = type;
      this.route = route;
    }
    public RouteHandler(Database db, String sql, String route){
      this.db = db;
      this.sql = sql;
      this.contentType = "db";
      this.route = route;
    }
    public void handle(HttpExchange exchange) {
      if(this.contentType.equals("db") ){
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        this.response = this.db.runSQL(this.sql,"json");
        //lg.dispMessage("Message to be sent: " + this.response, "info");
      }
      else if(this.contentType.equals("json") ){
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        this.response = Input.readFile(this.file);
      }
      else if((this.contentType.indexOf("text") != -1) && (this.file != null)){
        exchange.getResponseHeaders().set("Content-Type", this.contentType);
        this.response = Input.readFile(this.file);
      }
      else if((this.contentType.indexOf("image") != -1) && (this.file != null)){
        exchange.getResponseHeaders().set("Content-Type", this.contentType);
        this.response = Input.readImage(this.file);
      }
      try{
        send(this.response,exchange,this.route);
      }catch(IOException e){
        lg.dispMessage("Handler: " + e.getMessage(), "error");
      }
      
      //System.out.println("");
    }
	public static Map<String, Object> parseParameters(String method, HttpExchange exchange) throws UnsupportedEncodingException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String query = null;
		if(method.equals("post")){
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            query = br.readLine();
		}else if(method.equals("get")){
			URI requestedUri = exchange.getRequestURI();
            query = requestedUri.getRawQuery();
		}

		if (query != null) {
			String pairs[] = query.split("&");		
			for (String pair : pairs) {
				String param[] = pair.split("=");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
		return parameters;
	}
	public static void send(String response, HttpExchange exchange, String routeStr) throws IOException{
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
    String remoteAddress;
    //System.out.println(response.length());
    //System.out.println(response.getBytes());
    //System.out.println(response.getBytes().length);
    try{
      String tempAddress = exchange.getRemoteAddress().getAddress().toString();
      remoteAddress = tempAddress.substring(tempAddress.indexOf("/") + 1);
    }
    catch(Exception e){
      lg.dispMessage(e.toString() + " ---- RouteHandler.java", "warn");
      remoteAddress = "IP Address Unavailable";
    }
    try{
      if((exchange.getResponseHeaders().getFirst("Content-Type")  == null) || (exchange.getResponseHeaders().getFirst("Content-Type").toString().indexOf("text") != -1)){
        lg.dispMessage("Response sent to Client (" + remoteAddress + ") ~ " + routeStr, "info");
      }
      else{
        lg.dispMessage("Response sent to Client (" + remoteAddress + ") ~ " + routeStr + " -- Type: " + exchange.getResponseHeaders().getFirst("Content-Type").toString(), "info");
      }
    }
    catch(Exception e){
      lg.dispMessage(e.toString() + " ---- RouteHandler.java", "warn");
    }
    try{
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
    
        os.write(response.getBytes());
        os.close();
    }catch(IOException e){
      lg.dispMessage("Send: " + e.getMessage(), "error");
    }
	}
}