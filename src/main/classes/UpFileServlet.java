import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "UpFileServlet", value = "/UpFileServlet")
public class UpFileServlet extends HttpServlet {
    Dao dao=new Dao();
    private static final long serialVersionUID = 1L;

    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";

    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    public void UpFiles(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException, InterruptedException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            PrintWriter writer = response.getWriter();
            writer.println("Error: 表单必须包含 enctype=multipart/form-data");
            writer.flush();
            return;
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 中文处理
        upload.setHeaderEncoding("UTF-8");

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;
        String fileName="";
        String filePath="";
        String TableName="";
        String FileType="";
        String time="";
        HttpSession session = request.getSession();
        String id=(String) session.getAttribute("id");
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        char[] nameArr=fileName.toCharArray();
                        String filename="";
                        for (int i = 0; i < nameArr.length; i++) {
                            if(nameArr[i]!='('&&nameArr[i]!=')'&&nameArr[i]!='-'&&nameArr[i]!='、')filename=filename+nameArr[i];
                        }
                        String[] spstr=filename.split("\\.");

                        StringBuffer strbuff = new StringBuffer();
                        System.out.println(spstr.length);
                        for (int i = 0; i < spstr.length-1; i++) {
                            strbuff.append(spstr[i]);
                        }
                        FileType=spstr[spstr.length-1];
                        System.out.println("文件类型："+FileType);
                        TableName=strbuff.toString();
                        time=dao.InsertToDatas(id,TableName+"."+FileType);
                        filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        // 在控制台输出文件的上传路径
                        System.out.println(filePath);
                        // 保存文件到硬盘
                        item.write(storeFile);

                        request.setAttribute("message",
                                "文件上传成功!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("文件上传出错");
        }

        ReadExcel r=new ReadExcel();

        if(FileType.equals("csv")){
            ReadCSV readcsv=new ReadCSV();
            List<List<String>> rowlist=readcsv.CSVToExcel(filePath);
            int thisid=dao.getThisID(id,time);
            dao.CreateDictionary(TableName,thisid,rowlist.get(0),rowlist.get(rowlist.size()-1));
            dao.InsertCsvToMysql(rowlist,TableName,thisid);
        }
        else if(FileType.equals("xlsx")||FileType.equals("xls")){
            List<List<Object>> rowList=r.ExcelToRowList(filePath);
            int thisid=dao.getThisID(id,time);
            dao.CreateExcelDictionary(TableName,thisid,rowList.get(0),rowList.get(rowList.size()-1));
            dao.CreateDataTable(TableName,rowList,thisid);
        }
        dao.UpdateState(id,time);
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("msg", "");
        ob.put("count",1);
        ob.put("data","'filename':'AAA'");
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String method=request.getParameter("method");
        if(method.equals("UpFiles")){
            try {
                UpFiles(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
