/*
Modification
------------

April 15, 2022 - EP
Add replace_all function to remove single and double quotes and backslash
character that was breaking the JSON interpolations

May 23, 2022 - EP
Modified replace_all function to remove any character with an ascii value 
less the 32.

*/

import java.sql.*;
import java.util.ArrayList;

public class Database{
    private String url;
    private Connection conn;
    private boolean debug;

    public Database(String url){
        this.url = url;
        this.debug = true;
        connect(); 
    }
    public void setDebug(boolean state){
        this.debug = state;
    }
    private boolean connect(){
        boolean success = true;
        try{
            //Create a connection to the database. If it doesn't exist, create the DB
            conn = DriverManager.getConnection(url);    
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
            success = false;
        } 
        return success;
    }
    private boolean close(){
       boolean success = true;
        try{
            conn.close();   
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
            success = false;
        }
        return success; 
    }
    public String selectData(String sql){
        String result = null;
        connect();
        //System.out.println(sql);
        try (Statement stmt  = conn.createStatement()){
            ResultSet rs    = stmt.executeQuery(sql);
            result = json(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            close();
        }
        return result;
    }
    /*runSQL(String):
      Used for most SQL statements such as create, update, insert and delete.
    */
    public boolean runSQL(String sql){
        boolean success = true;
        connect();
        try(Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();)
        {	
            stmt.execute(sql);
            //conn.commit(); 	  
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
            success = false;
        }finally {
            close();
        }
        return success;
    }
    /* runSQL(String,String):
       Used for select statements which return information in a particular format. 
       Formats include json, csv or  table-<size>.  When using the table format you have to 
       provide a size for the table, table-auto makes the columns autofit the data in the column.
    */
    public String runSQL(String sql, String format){
        String result = null;
        connect();
        try (Statement stmt  = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            if(format.indexOf("table") != -1){
                String colWidth = format.substring(format.indexOf("-")+1);
                result = table(rs,colWidth);
            }else if(format.equals("json")){
                result = json(rs);
            }else if(format.equals("csv")){
                result = csv(rs);
            }
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
        } finally {
            close();
        }
        return result;
    }

    public String[][] runSQL(String sql, int arrayArg){
        String[][] result;
        connect();
        try (Statement stmt  = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            result = array(rs, sql);
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
            result = new String[0][];
        } finally {
            close();
        }
        return result;
    }
    
    public String pad(String text, int width){
        //Ensure the text has an even size
        String s = text.length() % 2 == 1? text += " ": text;
        //Truncates the text by two if the text is larger than the width 
        s = text.length() >= width ? text.substring(0,width - 2): text;
        int diff = width - s.length();
        int padSize = diff / 2;
        String padding = new String(new char[padSize]).replace("\0", " ");
        return padding + s + padding;
    }

    /* array(ResultSet)
       Return a resultset as array. This method was created to compare 2 values.
    */
    private String[][] array(ResultSet rs, String sql){
        String[][] result;
        try{                  
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            String sqlCount = "SELECT count(*) FROM " + sql.substring(sql.indexOf("FROM") + 5);
            String reply = this.runSQL(sqlCount, "csv");
            int rows = Integer.parseInt(reply.substring(reply.indexOf(",")+2, reply.length()-2));
            //System.out.println(rows);

            result = new String[rows][columnCount];

            String field;
            int index = 0;
            //Create Column Headers
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    field = metadata.getColumnName(i);
                    result[index][i-1] = rs.getString(field).replace(",","");
                }
                index ++;
            }
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
            result = new String[0][];
        }
        return result;
	}  
    /* json(ResultSet)
       Return a resultset as an array of JSON.  This method was created to be
       used in conjunction with HTTPServer in order to deliver information through
       an API.  Quotations are removed from value fields in order to produce a properly 
       formed JSON output.
    */
    private String json(ResultSet rs){
		String result = "";
        try{               
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();   

            String field, value, build = "[";
            
            while (rs.next()) {
                build += "{";
                for (int i = 1; i <= columnCount; i++) {
                    field = metadata.getColumnName(i);
                    //Retrieve the value and remove "" which cause a problem with the JSON format         
                    if(rs.getString(field) !=null){
                      value = rs.getString(field).replace("\"","");
                    }
                    else{
                      value ="";
                    }
                    value= replace_all(value);
                    build += "\"" + field + "\":\"" + value + "\",";
                }
                build = build.substring(0,build.length()-1) + "},";
            }
            result = build.substring(0,build.length()-1) + "]";
            if(result.equals("]")) result = "[]";
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
        }
        return result;
	}
    /* csv(ResultSet)
       Return a resultset in a csv format.  Commas are removed from value fields
       in order to produce a properly formed csv output.
    */
    private String csv(ResultSet rs){
		String result = "";
        try{                  
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();   

            String field, value;
            //Create Column Headers
            for (int i = 1; i <= columnCount; i++) {
                field = metadata.getColumnName(i);
                result += field + "," ;
            }
            result += "\n";
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    field = metadata.getColumnName(i);
                    value = rs.getString(field).replace(",","");
                    result += value + ",";
                }
                result += "\n";
            }
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
        }
        return result;
	}  
    /* table(Result,String):
       Return a resultset in a table format.  Method accepts the size of the column.  If auto
       is passed as the size, each column will be autofit to the longest data in the column.
    */
    private String table(ResultSet rs, String colWidth){
		String result = "";
        try{
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount(); 
            ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
            ArrayList<String> row;
            int[] maxWidths = new int[columnCount]; 
            String field, value;
            //Determine maxwidth for each column and store data
            row = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++) {
                field = metadata.getColumnName(i);
                row.add(field);
                maxWidths[i - 1] = Math.max(maxWidths[i - 1], field.length());
            }
            data.add(row);
            while (rs.next()) {
                row = new ArrayList<String>();
                for (int i = 1; i <= columnCount; i++) {
                    field = metadata.getColumnName(i);
                    value = rs.getString(field);
                  //System.out.println("i: "+i+" value: "+value);
                  if(value==null){
                    value="";
                  }
                    row.add(value);
                    maxWidths[i - 1] = Math.max(maxWidths[i - 1], value.length());
                }   
                data.add(row);
            }
            
            if(colWidth.equals("auto")){
                for(int i = 0; i < columnCount;i++){
                    if(maxWidths[i] % 2 == 1){
                        maxWidths[i]++;
                    } 
                }
            }else{
                //Set the maxWidth of each column to the value passed in
                int width = Integer.parseInt(colWidth); 
                if(width % 2 == 1){
                    width ++;
                }
                for(int i = 0; i < columnCount;i++){
                    maxWidths[i] = width;
                }
            }
            
            String rowSeparator = "+";
            for(int i = 0; i < columnCount; i++){
                for(int j = 0; j < maxWidths[i]+2; j++){
                    rowSeparator += "-";
                }
                rowSeparator += "+";
            }

            //Create Column Headers
            String header = "|";
            row = data.get(0);
            for (int i = 0; i < columnCount; i++) {
                header += pad(row.get(i),maxWidths[i]+2) + "|" ;
            }
            result = rowSeparator + "\n" + header + "\n" + rowSeparator + "\n";

            //Create Data Rows
            for(int i = 1; i < data.size(); i++){
                row = data.get(i);
                result += "|";
                for(int j = 0; j < row.size(); j++){                
                    result += pad(row.get(j),maxWidths[j]+2) + "|";
                }
                result += "\n" + rowSeparator + "\n";
            }
            result += data.size() - 1 + " records found.\n";
        } catch (SQLException e) {
            if(this.debug) System.out.println(e.getMessage());
        }
        return result;
	} 

  
  private static String replace_all(String s){
    String build="";
      if(s==null){
        return("");
      }
      for(int i=0; i< s.length(); i++){
        if(s.charAt(i)=='"')
          build+="'";
        else if (s.charAt(i)=='\\')
          build+="-";
        else if (s.charAt(i) < 32)
          build+="";
        else
          build+=s.charAt(i);
      }

    return build;
  }
}