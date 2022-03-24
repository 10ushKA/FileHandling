package by.bushylo.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class FileLocationContextListener implements ServletContextListener {
// TO MAKE reading of the parameter of the Context for locating of th f. and creating of an Obj. from File
    //DURING THE INITIALIZATION OF THE CONTEXT
    //1. set an AbsolutePath of the catalog and Obj. File as an attribute of Context(which gonna be used by other servlets)


    public void contextInitialized(ServletContextEvent sce) {
        String rootPath = System.getProperty("catalina.home");
        /*catalina.home points to the location of the common information.
          catalina.base points to the directory where all the instance specific information are held.
          So you have 1 home and can have more than 1 base.*/

        /*CATALINA_HOME: Represents the root of your Tomcat installation, for example /home/tomcat/apache-tomcat-9.0. 10
         CATALINA_BASE: Represents the root of a runtime configuration of a specific Tomcat instance.*/

        ServletContext ctx = sce.getServletContext();
        String relativePath = ctx.getInitParameter("tempfile.dir");
        File file = new File(rootPath + File.separator + relativePath);

        if(!file.exists()){file.mkdir();}

        System.out.println("File Directory created to be used for storing files");
        ctx.setAttribute("FILES_DIR_FILE", file);
        ctx.setAttribute("FILES_DIR", rootPath + File.separator + relativePath);

    }

    public void contextDestroyed(ServletContextEvent sce) {
//here should be cleaning up
    }
}
