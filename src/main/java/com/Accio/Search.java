package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try{
            Connection connection = DatabaseConnection.getConnection();
            //add keyword (Insert data )into history table
            // preparedStarement --> write insertion command from the java
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?,?)");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/SearchEngineAccio/Search?keyword="+keyword);
//            preparedStatement.setString(3, text);
            preparedStatement.executeUpdate();
            //get result from pages table
            //excute a query using connection method and createStatement to create statement
            // when ever we excute a query store in resultset it is linked list type show we use next
            ResultSet resultSet =  connection.createStatement().executeQuery("select pageTitle,pageLink,(length(lower(pageData))-length(replace(lower(pageData),'"+keyword+"',\"\")))/length('"+keyword+"') as countoccurence from pages order by countoccurence desc limit 30;");
           // get resultSet from database  converted into arrayList  and this arrayList  passed to the frontend part
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            while (resultSet.next()){
                SearchResult searchResult = new SearchResult();
                searchResult.setPageTitle(resultSet.getString("pageTitle"));
                searchResult.setPageLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }
            for(SearchResult result:results){
                System.out.println(result.getPageLink()+" "+result.getPageTitle()+"\n");
            }
            //forward all value(request) to frontend part in tabular result
            request.setAttribute("results",results);
            request.getRequestDispatcher("/search.jsp").forward(request,response);
            //set content type of response
            response.setContentType("text/html");
            // get writer to write content in response
            PrintWriter out = response.getWriter();
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        catch (ServletException servletException){
            servletException.printStackTrace();
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
