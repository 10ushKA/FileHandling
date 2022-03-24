package by.bushylo.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

@WebServlet(urlPatterns = "/UploadDownloadServlet")
public class UploadDownloadServlet extends HttpServlet {
    private ServletFileUpload uploader = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
//overriding for initialisation of the Obj of DiskFileItemFactory,
//which we gonna use in doPost for uploading of the file to server's catalog

        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        File fileDir = (File)getServletContext().getAttribute("FILE_DIR_FILE");
        fileFactory.setRepository(fileDir);
        this.uploader = new ServletFileUpload(fileFactory);
    }

//as long as the file uploaded we send resp to the client with URL-address for downloading the file

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//as we added as a parameter the name of the f. to URl-address, we can use doGet fo uploading the f.
        //for servlet's uploading first:
        // open InputStream,
        // use ServletContext.getMimeType to set MIMe-type as the resp content type
        //In order for the client to understand that we are sending a f. in resp, we need to set the header "Content-Disposition" with the value "attachment"; file name = "file name".
        //thaan we read the contents of the file from the InputStream and write it to the ServletOutputStream and clear the output for the client.

        File fileName = new File(req.getParameter("fileName"));
        if(fileName == null || fileName.equals("")){
            throw new ServletException("File Name can't be null or empty");
        }
        File file = new File(req.getServletContext().getAttribute("FILE_DIR") + File.separator + fileName);
        if(!file.exists()){
            throw new ServletException("File doesn't exist on server");
        }
        System.out.println("File location on server: " + file.getAbsolutePath());
        ServletContext ctx = getServletContext();
        InputStream fileInputStream = new FileInputStream(file);

        String mimeType = ctx.getMimeType(file.getAbsolutePath());
        resp.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        //application/octet-stream - undefined binary data, browsers, as a rule, will not try to process it in any way, but will call a "Save As" dialog box for it
        resp.setContentLength((int)file.length());
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");

        ServletOutputStream os = resp.getOutputStream();
        byte[] bufferData = new byte[1024];
        int read = 0;
        while((read = fileInputStream.read(bufferData)) != -1){
            os.write(bufferData,0,read);
        }
            //Writes len bytes(read) from the specified byte array(bufferData) starting at offset off(here = 0) to this output stream
            os.flush();//writes the content of the buffer to the destination and makes the buffer empty for further data to store but it does not closes the stream permanently
            // That means you can still write some more data to the stream
            os.close();//so we have to close the stream
            fileInputStream.close();
            System.out.println("File downloaded at client successfully");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//here we use the Obj  (which we initialised in overrided init()) for uploading of the f. to the server's catalog
    if (!ServletFileUpload.isMultipartContent(req)){//http request can only have one request and one response. The way around this limitation is the multipart
        throw new ServletException("Content type is not multipart/form-data");
    }

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    out.write("<html><head></head><body>");
    try{
        List<FileItem> fileItemList = uploader.parseRequest(req);
        Iterator<FileItem> fileItemIterator = fileItemList.iterator();//iterator() enables u to cycle through a collection, obtaining or removing elements.
        while(fileItemIterator.hasNext()) {
            FileItem fileItem = fileItemIterator.next();
            System.out.println("Field Name = " + fileItem.getFieldName());
            System.out.println("File Name = " + fileItem.getName());
            System.out.println("Content type  = " + fileItem.getContentType());
            System.out.println("Size in bytes = " + fileItem.getSize());


            File file = new File(req.getServletContext().getAttribute("FILE_DIR") + File.separator + fileItem.getName());
            System.out.println("Absolute path at server = " + file.getAbsolutePath());
            fileItem.write(file);
            out.write("File" + file.getName() + "successfully uploaded");
            out.write("<br>");
            out.write("<a href=\"UploadDownloadServlet?fileName="+fileItem.getName()+"\">Download "+fileItem.getName()+"</a>");
        }
    }catch (FileUploadException e){
        out.write("Exception in uploading file");
    } catch (Exception e) {
        out.write("Exception in uploading file");
    }
    }
}
