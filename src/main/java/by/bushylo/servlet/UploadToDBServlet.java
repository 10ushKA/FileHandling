package by.bushylo.servlet;

import by.bushylo.db.ConnectionPool;



import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



@WebServlet(urlPatterns = "/UploadToDBServlet")
@MultipartConfig(
  fileSizeThreshold = 1024*1024*2,// 2MB
  maxFileSize = 1024*1024*10,
  maxRequestSize = 1024*1024*50
)
public class UploadToDBServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getServletContext().getRequestDispatcher("/WEB-INF/uploadToDB.jsp");
        requestDispatcher.forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection connection;
        try{
            connection = ConnectionPool.CONNECTION_POOL.getConnection();
            connection.setAutoCommit(false);
            String description = req.getParameter("description");

            //Part list(multi files)
            for(Part part : req.getParts()){
                String fileName = extractFileName(part);

                if(fileName != null & fileName.length() > 0){
                    InputStream is = part.getInputStream();
                    this.writeToDB(connection, fileName, is, description);
                }
            }
            connection.commit();


            resp.sendRedirect(req.getContextPath() + "/UploadToDBResultServlet");
        }catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage","Error:" + e.getMessage());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/uploadToDBResults.jsp");
            dispatcher.forward(req,resp);
        }finally {
            try {
                ConnectionPool.CONNECTION_POOL.closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractFileName(Part part){
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if(s.trim().startsWith("fileName")){
                String clientFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                clientFileName = clientFileName.replace("\\", "/");
                int i = clientFileName.lastIndexOf('/');
                return clientFileName.substring(i + 1);
            }
        }
        return null;
    }

    private void writeToDB(Connection connection, String fileName, InputStream is, String description) throws SQLException {
        String sql = "insert into \"attachment\"(id, file_name, file_data, description) values (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);

        Long id = this.getMaxAttachmentId(connection) + 1;
        statement.setLong(1, id);
        statement.setString(2,fileName);
        statement.setBlob(3,is);
        statement.setString(4,description);
        statement.executeQuery();
    }

    private Long getMaxAttachmentId(Connection connection) throws SQLException {
        String sql = "select max(a.id) from from \"attachment\" as a";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            long max = rs.getLong(1);
            return max;
        }
        return null;//it has to be return 0L; BUt smth goes wrong
    }
}
