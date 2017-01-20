package com.tqdev;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/*"}, loadOnStartup = 1)
public class CrudApiServlet extends HttpServlet 
{
  private static final long serialVersionUID = 1L;

  private JSONObject readJsonFromReader(int length,BufferedReader reader) throws IOException
  {
    if (length<=0) return null;
    JSONParser parser = new JSONParser();
    JSONObject obj;
    try {
      obj = (JSONObject)parser.parse(reader);
    } catch (ParseException e) {
      System.out.println("readJsonFromReader:"+e);
      obj = new JSONObject();
    }
    return obj;
  }

  private Connection mysqlConnect(String connectString)
  {
    Connection link;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
      link = DriverManager.getConnection(connectString);
    } catch (Exception e) {
      System.out.println("mysqlConnect:"+e);
      link = null;
    }
    return link;
  }

  @Override 
  public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    // get the HTTP method, path and body of the request
    String method = req.getMethod();
    String[] request = req.getPathInfo().replaceAll("/$|^/","").split("/");
    JSONObject input = readJsonFromReader(req.getContentLength(),req.getReader());
    // connect to the mysql database
    Connection link = mysqlConnect("jdbc:mysql://localhost/php-crud-api?user=php-crud-api&password=php-crud-api&characterEncoding=UTF-8");
    // retrieve the table and key from the path
    String table = request[0].replaceAll("[^a-zA-Z0-9_]+","");
    int key = (request.length>1?Integer.parseInt(request[1]):-1);
    // escape the columns and values from the input object
    String[] columns = input==null?(new String[0]):(String[])input.keySet().toArray();
    for (int i=0;i<columns.length;i++) {
      columns[i] = columns[i].replaceAll("[^a-zA-Z0-9_]+","");
    }
    // build the SET part of the SQL command
    String set = "";
    for (int i=0;i<columns.length;i++) {
      set+=(i>0?",":"")+"`"+columns[i]+"`=?";
    }
    // create SQL based on HTTP method
    String sql="";
    if (method=="GET") {
        sql = "select * from `"+table+"`"+(key>0?" WHERE id="+key:"");
    } else if (method=="PUT") {
        sql = "update `"+table+"` set "+set+" where id="+key;
    } else if (method=="POST") {
        sql = "insert into `"+table+"` set "+set;
    } else if (method=="DELETE") {
        sql = "delete `"+table+"` where id="+key;
    }
    try {
      // execute SQL statement
      PreparedStatement statement = link.prepareStatement(sql);
      for (int i=0;i<columns.length;i++) {
        statement.setObject(i, input.get(columns[i]));
      }
      // print results, insert id or affected row count
      PrintWriter w = resp.getWriter();
      if (method == "GET") {
        ResultSet result = statement.executeQuery();
        ResultSetMetaData meta = result.getMetaData();
        int colCount = meta.getColumnCount();
        if (key<0) w.write('[');
        int row=0;
        while (result.next()) {
          if (row>0) w.write(',');
          w.write('{');
          for (int col=1;col<=colCount;col++) {
            if (col>1) w.write(',');
            JSONValue.writeJSONString(meta.getColumnName(col),w);
            w.write(':');
            JSONValue.writeJSONString(result.getObject(col),w);
          }
          w.write('}');
          row++;
        }
        if (key<0) w.write(']');
      } else if (method == "POST") {
        statement.executeUpdate(sql);
        ResultSet result = statement.getGeneratedKeys();
        result.next();
        JSONValue.writeJSONString(result.getObject(1),w);
      } else {
        JSONValue.writeJSONString(statement.executeUpdate(sql),w);
      }
      // close mysql connection
      link.close();
    }
    catch (SQLException e) {
      System.out.println("SQLException:"+e);
    }
  }
}
