import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "servlet", value = "/servlet")
public class servlet extends HttpServlet {
    Dao dao=new Dao();
    Utils utils=new Utils();
    public void Login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String id = request.getParameter("id");
        String password = request.getParameter("password");
        String res = dao.login(Integer.parseInt(id), password);
        System.out.println(res + "RESSS");
        if (res.equals("登录错误")) {
            PrintWriter out = response.getWriter();
            out.print("<script>alert('登录失败!');window.location.href='Login.html'</script>");
        }
        else {
            HttpSession session = request.getSession();
            session.setAttribute("id", id);
            if(dao.StartLog)dao.Log(id,dao.LOGIN,"用户"+id+"登录系统");
            PrintWriter out = response.getWriter();
            out.print("<script>window.location.href='PlatformMain.html'</script>");
        }
    }
    //获得数据集字段数量
    public void getDataClSize( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String ID=request.getParameter("TabID");
        int clsize=dao.getLocalTabCl(ID).size();
        PrintWriter out = response.getWriter();
        out.write(clsize+"".toString());

    }
    //将数据库数据在前台显示
    public void showExcelData( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        int page=Integer.parseInt(request.getParameter("page"));
        int pagesize=Integer.parseInt(request.getParameter("limit"));
        int clsize=dao.getLocalTabCl(TabID).size();
        List<List<String>> bigList=dao.getExcelData(TabID,clsize);
        JSONArray json=new JSONArray();
        int lastnum=0;
        if(bigList.size()<page*pagesize){
            lastnum=bigList.size();
        }
        else {
            lastnum=page*pagesize;
        }
        for(int i=(page-1)*pagesize;i<lastnum;i++){
            JSONObject ob=new JSONObject();
            for(int j=0;j<clsize;j++){
                ob.put("cl"+j,bigList.get(i).get(j).toString());
            }
            json.add(ob);
        }
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("size",clsize);
        ob.put("msg", "");
        ob.put("count",bigList.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //历史数据集表格
    public void HistoryTab( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String id=(String) session.getAttribute("id");
        List<HistoryTabBean> list=dao.HisTab(id);
        JSONArray json=new JSONArray();
        for(int i=0;i<list.size();i++){
            JSONObject ob=new JSONObject();
            ob.put("id",list.get(i).getId());
            ob.put("userid",list.get(i).getUserid());
            ob.put("showid",(i+1));
            ob.put("dataname",list.get(i).getDataname());
            ob.put("time",list.get(i).getTime());
            ob.put("state",list.get(i).getState());
            ob.put("beizhu",list.get(i).getBeizhu());
            json.add(ob);
        }
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("msg", "");
        ob.put("count",list.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //修改数据集备注
    public void ChangeDataBeizhu( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String value=request.getParameter("value");
        dao.ChangeDataBeizhu(TabID,value);
    }
    //删除一个数据集及其字典
    public void DeleteOneHistory( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabId=request.getParameter("tabid");
        String TabName=request.getParameter("tabname");
        char[] nameArr=TabName.toCharArray();
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
        TabName=strbuff.toString();
        dao.DeleteOnHistory(TabId,TabName);
    }
    //显示字典内容
    public void showDictionary( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabId = request.getParameter("tabid");
        String TabName = request.getParameter("tabname");
        char[] nameArr=TabName.toCharArray();
        String filename="";
        for (int i = 0; i < nameArr.length; i++) {
            if(nameArr[i]!='('&&nameArr[i]!=')'&&nameArr[i]!='-'&&nameArr[i]!='、')filename=filename+nameArr[i];
        }
        String[] spstr=filename.split("\\.");
        StringBuffer strbuff = new StringBuffer();
        for (int i = 0; i < spstr.length-1; i++) {
            strbuff.append(spstr[i]);
        }
        TabName=strbuff.toString();
        List<DictionaryBean> list=dao.GetDictionary(TabId,TabName);
        JSONArray json=new JSONArray();
        for(int i=0;i<list.size();i++){
            JSONObject ob=new JSONObject();
            ob.put("showid",i+1);
            ob.put("字段名称",list.get(i).getZdmc());
            ob.put("字段类型",list.get(i).getZdlx());
            ob.put("备注",list.get(i).getBeizhu());
            ob.put("字段长度",list.get(i).getZdcd());
            ob.put("小数精度",list.get(i).getXsjd());
            json.add(ob);
        }
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("msg", "");
        ob.put("count",list.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //删除字典中的一个字段
    public void DeleteOneZiDuan( HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String zdmc=request.getParameter("zdmc");
        String TabID=request.getParameter("tabid");
        String TabName=request.getParameter("tabname");
        String showid=request.getParameter("showid");
        char[] nameArr=TabName.toCharArray();
        String filename="";
        for (int i = 0; i < nameArr.length; i++) {
            if(nameArr[i]!='('&&nameArr[i]!=')'&&nameArr[i]!='-'&&nameArr[i]!='、')filename=filename+nameArr[i];
        }
        String[] spstr=filename.split("\\.");
        StringBuffer strbuff = new StringBuffer();
        for (int i = 0; i < spstr.length-1; i++) {
            strbuff.append(spstr[i]);
        }
        TabName=strbuff.toString();
        dao.DeleteOneZiDuan(zdmc,TabID,TabName,showid);
    }

    public void ChangeDictionaryOne(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String ziduan=request.getParameter("zd");
        String zdmc=request.getParameter("zdmc");
        String value=request.getParameter("value");
        String TabID=request.getParameter("tabid");
        String TabName=request.getParameter("tabname");
        char[] nameArr=TabName.toCharArray();
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
        TabName=strbuff.toString();
        dao.ChangeDictionaryOne(TabID,TabName,ziduan,zdmc,value);
    }
    //数据集备份
    public void DataBackUp(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String DataName=request.getParameter("DataName");
        String DataCreateTime=request.getParameter("Time");
        HttpSession session = request.getSession();
        String id=(String) session.getAttribute("id");
        dao.DataBackUp(id,DataName,Integer.parseInt(TabID));
    }
    public void showDataBackUp(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String id=(String) session.getAttribute("id");
        List<BackUpBean> list=dao.showDataBackUp(id);
        JSONArray json=new JSONArray();
        for(int i=0;i<list.size();i++){
            JSONObject ob=new JSONObject();
            ob.put("id",list.get(i).getId());
            ob.put("userid",list.get(i).getUserid());
            ob.put("showid",(i+1));
            ob.put("dataname",list.get(i).getDataname());
            ob.put("time",list.get(i).getTime());
            ob.put("state",list.get(i).getState());
            ob.put("beizhu",list.get(i).getBeizhu());
            ob.put("yuanid",list.get(i).getYuanid());
            json.add(ob);
        }
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("msg", "");
        ob.put("count",list.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //删除一个备份数据集
    public void DeleteOneBackUp(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String BFID=request.getParameter("BFID");
        String BFDataName=request.getParameter("BFDataName");
        dao.DeleteOneBackUp(BFID,BFDataName);
    }
    //将备份数据集返回到操作数据集
    public void DataBackUpToData(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("调用备份数据返回");
        String BFID=request.getParameter("BFID");
        String BFDataName=request.getParameter("BFDataName");
        String Yuanid=request.getParameter("yuanid");
        String NowTime=utils.GetCurrentTime();
        System.out.println("yuanid"+Yuanid);
        HttpSession session = request.getSession();
        String Userid=(String) session.getAttribute("id");
        List<String> clname=dao.getColumnNames("bf"+Yuanid+utils.DelHouZhui(BFDataName));
        dao.DataBackUpToData(NowTime,BFID,BFDataName,Yuanid,Userid,clname);

    }
    //修改备份备注
    public void ChangeBackUpBeizhu(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String BFID=request.getParameter("BFID");
        String value=request.getParameter("value");
        dao.ChangeBackUpBeizhu(BFID,value);
    }
    //数据清洗数据集表格
    public void DataCleanTab(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String Userid=(String) session.getAttribute("id");
        List<HistoryTabBean> list=dao.DataCleanTab(Userid);
        JSONArray json=new JSONArray();
        for(int i=0;i<list.size();i++){
            JSONObject ob=new JSONObject();
            ob.put("id",list.get(i).getId());
            ob.put("showid",(i+1));
            ob.put("dataname",list.get(i).getDataname());
            ob.put("beizhu",list.get(i).getBeizhu());
            json.add(ob);
        }
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("msg", "");
        ob.put("count",list.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //去重时获得一个数据集的第一行信息
    public void BaseIDGetCl(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String DataName=request.getParameter("DataName");
        List<String> clName=dao.getColumnNames("data"+TabID+utils.DelHouZhui(DataName));
        List<String> list=dao.BaseIDGetCl(TabID,utils.DelHouZhui(DataName),clName);
        JSONArray json=new JSONArray();
        for(int i=0;i<list.size();i++){
            JSONObject ob=new JSONObject();
            ob.put("id",i);
            ob.put("name",list.get(i));
            json.add(ob);
        }
        PrintWriter out = response.getWriter();
        out.write(json.toString());
    }
    //去重
    public void Removeduplicate(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String DataName=request.getParameter("DataName");
        String cl=request.getParameter("cl");
        List<String> clName=dao.getColumnNames("data"+TabID+utils.DelHouZhui(DataName));
        dao.QUCHONG(TabID,utils.DelHouZhui(DataName),Integer.parseInt(cl),clName.size());
        PrintWriter out = response.getWriter();
        out.write("Finish");
    }
    //根据字段获得缺失值的表格
    public void showExcelQueShi(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String cl=request.getParameter("cl");
        int page=Integer.parseInt(request.getParameter("page"));
        int pagesize=Integer.parseInt(request.getParameter("limit"));
        int clsize=dao.getLocalTabCl(TabID).size();
        List<List<String>> bigList=dao.showExcelQueShi(TabID,clsize,cl);
        JSONArray json=new JSONArray();
        int lastnum=0;
        if(bigList.size()<page*pagesize){
            lastnum=bigList.size();
        }
        else {
            lastnum=page*pagesize;
        }
        for(int i=(page-1)*pagesize;i<lastnum;i++){
            JSONObject ob=new JSONObject();
            for(int j=0;j<clsize;j++){
                ob.put("cl"+j,bigList.get(i).get(j).toString());
            }
            json.add(ob);
        }
        System.out.println("servlet调用showExcel");
        JSONObject ob=new JSONObject();
        ob.put("code", 0);
        ob.put("size",clsize);
        ob.put("msg", "");
        ob.put("count",bigList.size());
        ob.put("data",json);
        PrintWriter out = response.getWriter();
        out.write(ob.toString());
    }
    //修改缺失字段的某个值
    public void ChangeOneQueShi(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String DataName=request.getParameter("DataName");
        String cl0=request.getParameter("cl0");
        String changecl=request.getParameter("changecl");
        String value=request.getParameter("value");
        dao.ChangeOneQueShi(TabID,utils.DelHouZhui(DataName),cl0,changecl,value);
    }
    //删除无意义的数据
    public void DelNoMeaing(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID=request.getParameter("TabID");
        String DataName=request.getParameter("DataName");
        String cl=request.getParameter("cl");
        String value=request.getParameter("value");
        int count=dao.DelNoMeaing(TabID,utils.DelHouZhui(DataName),cl,value);
        PrintWriter out = response.getWriter();
        out.write(count+"");
    }
    //数据列拼接
    public void PinJie(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        String TabID = request.getParameter("TabID");
        String DataName = request.getParameter("DataName");
        String cl1=request.getParameter("cl1");
        String cl2=request.getParameter("cl2");
        String cl3=request.getParameter("cl3");
        dao.pinjie(TabID,utils.DelHouZhui(DataName),cl1,cl2,cl3);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String method=request.getParameter("method");
        if(method.equals("Login")){
            try {
                Login(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("PinJie")){
            try {
                PinJie(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DelNoMeaing")){
            try {
                DelNoMeaing(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("ChangeOneQueShi")){
            try {
                ChangeOneQueShi(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("showExcelQueShi")){
            try {
                showExcelQueShi(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("Removeduplicate")){
            try {
                Removeduplicate(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("BaseIDGetCl")){
            try {
                BaseIDGetCl(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DataCleanTab")){
            try {
                DataCleanTab(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DataBackUpToData")){
            try {
                DataBackUpToData(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("ChangeBackUpBeizhu")){
            try {
                ChangeBackUpBeizhu(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DeleteOneBackUp")){
            try {
                DeleteOneBackUp(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("showDataBackUp")){
            try {
                showDataBackUp(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DataBackUp")){
            try {
                DataBackUp(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DeleteOneZiDuan")){
            try {
                DeleteOneZiDuan(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("ChangeDictionaryOne")){
            try {
                ChangeDictionaryOne(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("showDictionary")){
            try {
                showDictionary(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("DeleteOneHistory")){
            try {
                DeleteOneHistory(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("ChangeDataBeizhu")){
            try {
                ChangeDataBeizhu(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("getDataClSize")){
            try {
                getDataClSize(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("showExcelData")){
            try {
                showExcelData(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("HistoryTab")){
            try {
                HistoryTab(request,response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
