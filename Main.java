//---------------Email Stuff---------------
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import org.json.*;
import java.text.*;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
//---------------Http Stuff---------------
import java.net.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.util.Map;
import java.security.*;
import java.util.Arrays;

// https://www.courier.com/guides/java-send-email/
class Main{
  public static JSONArray emailList;
  public static Thread emailChecker;
  private static Logger lg = new Logger();
  public static void main(String[] args) throws Exception {
    final String email = "sendtofutureme.elijah@gmail.com";
    final String password = System.getenv("email_password");
    final String host = "smtp.gmail.com";
    
    Properties properties = new Properties();
    properties.put("mail.transport.protocol", "smtp");
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.starttls.enable","true");
    properties.put("mail.smtp.ssl.enable", "true");
    properties.put("mail.smtp.port", "465");
    properties.put("mail.smtp.socketFactory.port", "465");
    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    properties.put("mail.smtp.socketFactory.fallback", "false");
    properties.put("mail.smtp.auth", "true");
    
    final Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(email, password);
      }
    });
    
    //---------------Below is a test---------------
    //System.out.println(sendEmail(session, email, "The sender of STFM email", "eren5836@baysidehighschool.org", "Hiyo", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    

    //Everything above is needed for initialization
    //---------------Type New Code Below---------------
    try{
      HttpRequests req = new HttpRequests("https://send-to-future-me.elijah15976.repl.co/unsent-messages", "POST");
      HttpResponse<String> res = req.sendRequest("secret="+System.getenv("email-manager_password"));
      Input.writeFile("emails.json", res.body().replace("&#13;", "\\n").replace("&#39;", "\\'").replace("&quot;", "\\\""));
    }
    catch(Exception e){
      lg.dispMessage(e.toString() + " ---- Main.java | Initialization", "error");
    }
    
    emailChecker = new Thread("emailChecker"){
      private volatile boolean flag = false;
      
      public void interrupt(){
        flag = true;
      }
      
      public void run(){
        lg.dispMessage("Email Checker has come back online", "info");
        String secret = System.getenv("email-manager_password");
        emailList = getStoredEmails();
        JSONArray sortedEmailList = sortEmailsByTime(emailList);
        JSONObject nextEmail = sortedEmailList.getJSONObject(0);
        SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        SimpleDateFormat sdformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dOg = new Date();
        Date dNow = new Date();

        try{
          dOg = sdformat.parse(nextEmail.getString("Date") + " " + nextEmail.getString("Time"));
        }
        catch(Exception e){
          lg.dispMessage("Error getting send date", "error");
          flag = true;
        }
        
        while(!flag){
          //TODO: Also be sure to fill out the other thread in "/update"
          try{
            String stringTempTime = LocalTime.now().toString();
            String stringTempDateTime = LocalDate.now().toString() + " " + stringTempTime.substring(0, stringTempTime.indexOf(".")-3);
            dNow = sdformat1.parse(stringTempDateTime);
          }
          catch(Exception e){
            lg.dispMessage("Error getting current date", "warn");
          }
          if(dOg.compareTo(dNow) <= 0){
            //TODO: Call to sendEmail function. Make a route for main server to change email status from pending(0) to sent(1). Then that route will call another route in this server to start the thread again
            try{
              HttpRequests req = new HttpRequests("https://send-to-future-me.elijah15976.repl.co/sent-message", "POST");
              HttpResponse<String> res = req.sendRequest("secret="+secret+"&messageId="+nextEmail.getString("ID")+"&statusToChangeTo=1");
              sendEmail(session, email, "From: "+nextEmail.getString("Email"), nextEmail.getString("Recipient"), nextEmail.getString("Sub"), nextEmail.getString("Msg"));
              switch(res.body()){
                case "bad":
                  lg.dispMessage("Bad parameters to /sent-message", "warn");
                  break;
                case "nothing give":
                  lg.dispMessage("Nothing given to /sent-message", "warn");
                default:
                  lg.dispMessage("Email sent update request sent", "info");
              }
            }
            catch(Exception e){
              lg.dispMessage("An email was not sent correctly", "crit");
            }
            
            flag = true;
          }
        }
      }
    };
    emailChecker.start();
    
    int port = 32000;
    HttpServer server = HttpServer.create(new InetSocketAddress(port),0);

    server.createContext("/", new RouteHandler("Hi, I'm here", "/"));

    server.createContext("/verification", new HttpHandler(){
      public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> parameters = RouteHandler.parseParameters("post",exchange);
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        
        String response;
        String sendEmail = "";
        String verificationCode = "";
        try{
          sendEmail = parameters.get("email").toString();
          verificationCode = parameters.get("code").toString();
        }
        catch(Exception e){
          lg.dispMessage("No email or code parameter", "warn");
        }
        if((!sendEmail.isEmpty()) &&
           (!verificationCode.isEmpty())){
          boolean sentStatus = sendEmail(session, email, "Send To Future Me", sendEmail, "Verify your account", "Please copy the following link and paste it into your browser to verify your account\nLink: https://send-to-future-me.elijah15976.repl.co/verify/"+verificationCode);
          if(sentStatus){
            response = "Email Sent";
          }
          else{
            response = "An error occurred";
          }
        }
        else{
          lg.dispMessage("A client found this link??", "warn");
          response = "How did you find this lmao";
        }
        //System.out.println(sendEmail + verificationCode);
        
        RouteHandler.send(response, exchange, "/verification");
      }
    });

    server.createContext("/update", new HttpHandler(){
      public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> parameters = RouteHandler.parseParameters("get",exchange);
        String response;
        String secret = System.getenv("email-manager_password");
        try{
          emailChecker.interrupt();
        }
        catch(Exception e){
          lg.dispMessage("Email checker is already offline", "info");
        }
        

        try{
          HttpRequests req = new HttpRequests("https://send-to-future-me.elijah15976.repl.co/unsent-messages", "POST");
          HttpResponse<String> res = req.sendRequest("secret="+secret);
          Input.writeFile("emails.json", res.body().replace("&#13;", "\\n").replace("&#39;", "\\'").replace("&quot;", "\\\""));
          
          emailChecker = new Thread("emailChecker"){
            private volatile boolean flag = false;
            
            public void interrupt(){
              flag = true;
            }
            
            public void run(){
              lg.dispMessage("Email Checker has come back online", "info");
              String secret = System.getenv("email-manager_password");
              emailList = getStoredEmails();
              JSONArray sortedEmailList = sortEmailsByTime(emailList);
              JSONObject nextEmail = sortedEmailList.getJSONObject(0);
              SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
              SimpleDateFormat sdformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
              Date dOg = new Date();
              Date dNow = new Date();
      
              try{
                dOg = sdformat.parse(nextEmail.getString("Date") + " " + nextEmail.getString("Time"));
              }
              catch(Exception e){
                lg.dispMessage("Error getting send date", "error");
                flag = true;
              }
              
              while(!flag){
                //TODO: Also be sure to fill out the other thread in "/update"
                try{
                  String stringTempTime = LocalTime.now().toString();
                  String stringTempDateTime = LocalDate.now().toString() + " " + stringTempTime.substring(0, stringTempTime.indexOf(".")-3);
                  dNow = sdformat1.parse(stringTempDateTime);
                }
                catch(Exception e){
                  lg.dispMessage("Error getting current date", "warn");
                }
                if(dOg.compareTo(dNow) <= 0){
                  //TODO: Call to sendEmail function. Make a route for main server to change email status from pending(0) to sent(1). Then that route will call another route in this server to start the thread again
                  try{
                    HttpRequests req = new HttpRequests("https://send-to-future-me.elijah15976.repl.co/sent-message", "POST");
                    HttpResponse<String> res = req.sendRequest("secret="+secret+"&messageId="+nextEmail.getString("ID")+"&statusToChangeTo=1");
                    sendEmail(session, email, "From: "+nextEmail.getString("Email"), nextEmail.getString("Recipient"), nextEmail.getString("Sub"), nextEmail.getString("Msg"));
                    switch(res.body()){
                      case "bad":
                        lg.dispMessage("Bad parameters to /sent-message", "warn");
                        break;
                      case "nothing give":
                        lg.dispMessage("Nothing given to /sent-message", "warn");
                      default:
                        lg.dispMessage("Email sent update request sent", "info");
                    }
                  }
                  catch(Exception e){
                    lg.dispMessage("An email was not sent correctly", "crit");
                  }
                  
                  flag = true;
                }
              }
            }
          };
          emailChecker.start();
          response = "";
        }
        catch(Exception e){
          lg.dispMessage(e.toString() + " ---- Main.java", "crit");
          response = "Critical Error";
        }
        
        RouteHandler.send(response, exchange, "/update");
      }
    });

    server.start();
    lg.dispMessage("Server is listening on port "+ port, "info");
  }

  public static JSONArray getStoredEmails(){
    String emailListString = Input.readFile("emails.json");
    return new JSONArray(emailListString);
  }

  public static JSONArray sortEmailsByTime(JSONArray emailList){
    JSONArray finalArray = new JSONArray();
    double[] dateTimeList = new double[emailList.length()];
    for(int i = 0; i<emailList.length(); i++){
      JSONObject tempIndex = emailList.getJSONObject(i);
      String[] dateArray = Format.formatDate(tempIndex.getString("Date"), "M/D/Y");
      String[] timeArray = Format.formatTime(tempIndex.getString("Time"), "H:M");
      String tempDateTime = dateArray[2].trim() + dateArray[0].trim() + dateArray[1].trim() + "." + timeArray[0].trim() + timeArray[1].trim();
      dateTimeList[i] = Double.parseDouble(tempDateTime.trim());
    }
    Arrays.sort(dateTimeList);
    for(int i = 0; i<dateTimeList.length; i++){
      //System.out.println(dateTimeList[i]);
      for(int j = 0; j<emailList.length(); j++){
        JSONObject tempIndex = emailList.getJSONObject(j);
        String[] dateArray = Format.formatDate(tempIndex.getString("Date"), "M/D/Y");
        String[] timeArray = Format.formatTime(tempIndex.getString("Time"), "H:M");
        String tempDateTime = dateArray[2].trim() + dateArray[0].trim() + dateArray[1].trim() + "." + timeArray[0].trim() + timeArray[1].trim();
        if(dateTimeList[i] == Double.parseDouble(tempDateTime.trim())){
          finalArray.put(tempIndex);
          break;
        }
      }
    }
    return finalArray;
  }

  private static boolean sendEmail(Session session, String sendFrom, String STFMSender, String sendTo, String sub, String msg){
    try{
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(sendFrom, STFMSender));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));
      message.setSubject(sub);
      message.setText(msg);

      Transport.send(message);
      return true;
    }
    catch(MessagingException mex){
      mex.printStackTrace();
      return false;
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }
}