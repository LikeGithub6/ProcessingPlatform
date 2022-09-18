import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    public final String UPFILE="上传文件";
    public final String DELETE="删除";
    public final String UPDATE="修改";
    public final String INSERT="新增";
    public final String FIND="查询";
    public final String LOGIN="登录";
    public boolean StartLog=false;
    Utils utils=new Utils();
    //记录日志
    public void Log(String ID,String Type,String Content) throws SQLException {
        String sql="insert into log (userid,time,type,content) values('"+ID+"','"+utils.GetCurrentTime()+"','"+Type+"','"+Content+"')";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Dbutil.close(rs, st, conn);
        }
    }
    //登录
    public String login(int Id, String Password) throws SQLException {
        String sql = "select * from user ";
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<UserBean> list = new ArrayList<>();
        UserBean bean = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String pass = rs.getString("password");
                String type = rs.getString("type");
                bean = new UserBean(id, pass, type);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).getId()==Id&&list.get(i).getPassword().equals(Password)){
                return list.get(i).getType();
            }
        }
        return "登录错误";
    }
    //上传数据后将记录写入数据库,此时状态为正在上传
    public String InsertToDatas(String ID,String TabName) throws SQLException {
        String time=utils.GetCurrentTime();
        String sql="insert into datas (userid,dataname,time,state) values('"+ID+"','"+TabName+"','"+time+"','"+"正在上传"+"')";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Dbutil.close(rs, st, conn);
        }
        return time;
    }
    //将状态改为上传成功
    public void UpdateState(String ID,String time){
        String sql="update datas set state='"+"上传完成"+"' where userid='"+ID+"' and time='"+time+"'";
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
    }
    //获得当前列的id
    public int getThisID(String ID,String time){
        String sql="select id from datas where userid="+ID+" and time='"+time+"'";
        int thisid=0;
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        PreparedStatement pStemt = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                thisid=rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        return thisid;
    }
    //上传数据集后将xlsx或xls数据写入数据库
    public void CreateDataTable(String dataName,List<List<Object>> rowlist,int id) throws SQLException {
        String sql="create table data"+id+dataName+" ( ";
        for(int i=0;i<rowlist.get(0).size()-1;i++){
            sql=sql+"cl"+i+" text,";
        }
        sql=sql+"cl"+(rowlist.get(0).size()-1)+" text )";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        for(int i=0;i<rowlist.size();i++) {
            sql = "insert into data" +id+ dataName + " values (";
            for(int j=0;j<rowlist.get(0).size()-1;j++){
                sql=sql+"'"+rowlist.get(i).get(j)+"',";
            }
            sql=sql+"'"+rowlist.get(i).get(rowlist.get(0).size()-1)+"')";
            try {
                st = conn.createStatement();
                st.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {


            }
        }
        Dbutil.close(rs, st, conn);

    }
    //将CSV写入MySQL
    public void InsertCsvToMysql(List<List<String>> rowlist,String dataName,int id) throws SQLException {
        System.out.println("rowlist第一行的数量:"+rowlist.get(0).size());
        String sql="create table data"+id+dataName+" ( ";
        for(int i=0;i<rowlist.get(0).size()-1;i++){
            sql=sql+"cl"+i+" text,";
        }
        sql=sql+"cl"+(rowlist.get(0).size()-1)+" text )";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        for(int i=0;i<rowlist.size();i++){
            sql = "insert into data" +id+ dataName + " values (";
            for(int j=0;j<rowlist.get(0).size()-1;j++){
                sql=sql+"'"+rowlist.get(i).get(j)+"',";
            }
            sql=sql+"'"+rowlist.get(i).get(rowlist.get(0).size()-1)+"')";
            try {
                st = conn.createStatement();
                st.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {


            }
        }
        Dbutil.close(rs, st, conn);
    }
    //获得本地数据库列名
    public List<String> getLocalTabCl(String ID){
        String sql="select dataname from datas where id="+ID;
        String tabname="";
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        PreparedStatement pStemt = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                tabname=rs.getString("dataname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        String[] spstr=tabname.split("\\.");

        StringBuffer strbuff = new StringBuffer();
        for (int i = 0; i < spstr.length-1; i++) {
            strbuff.append(spstr[i]);
        }
        tabname=strbuff.toString();
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接

        String tableSql = "SELECT * FROM data"+ID + tabname;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    Dbutil.close(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        Dbutil.close(st, conn);
        return columnNames;
    }
    //根据数据集ID的到数据集名称
    static String getDataTabName(String TabID){
        String sql="select dataname from datas where id="+TabID;
        String tabname="";
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        PreparedStatement pStemt = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                tabname=rs.getString("dataname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        String[] spstr=tabname.split("\\.");

        StringBuffer strbuff = new StringBuffer();
        for (int i = 0; i < spstr.length-1; i++) {
            strbuff.append(spstr[i]);
        }
        tabname=strbuff.toString();
        return tabname;
    }
    //获得数据库中数据集的数据
    public List<List<String>> getExcelData(String TabID,int clsize){
        String tabname=getDataTabName(TabID);
        List<List<String>> bigList=new ArrayList<>();
        String sql="select * from data"+TabID+tabname;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        ResultSet rs=null;
        try {
            st=conn.createStatement();
            st.executeQuery(sql);
            rs=st.executeQuery(sql);
            while(rs.next()) {
                List<String> list=new ArrayList<>();
                for(int i=0;i<clsize;i++){
                    list.add(rs.getString("cl"+i));
                }
                bigList.add(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        System.out.println("biglist的json数据为:"+bigList.get(1).get(1));
        return bigList;

    }
    //获得历史数据集表格
    public List<HistoryTabBean> HisTab(String userid){
        int UID=Integer.parseInt(userid);
        String sql="select * from datas where userid="+userid;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        List<HistoryTabBean> list=new ArrayList<>();
        ResultSet rs=null;
        HistoryTabBean bean=null;
        try {
            st=conn.createStatement();
            st.executeQuery(sql);
            rs=st.executeQuery(sql);
            while(rs.next()) {
                int id=rs.getInt("id");
                String dataname=rs.getString("dataname");
                String time=rs.getString("time");
                String state=rs.getString("state");
                String beizhu=rs.getString("beizhu");
                bean =new HistoryTabBean(id,UID,dataname,time,state,beizhu);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        return list;
    }
    //修改数据集备注
    public void ChangeDataBeizhu(String TabID,String value){

        String sql="update datas set beizhu='"+value+"' where id="+TabID+"";
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
    }
    //生成数据字典
    public void CreateDictionary(String TabName,int id,List<String> row1,List<String> rowlast) throws SQLException {
        String sql="create table dy"+id+TabName+" (字段名称 varchar(100),字段类型 varchar(20),备注 varchar(100),字段长度 varchar(10),小数精度 varchar(10))";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        String type="";
        for(int i=0;i<row1.size();i++){
            if(utils.isInteger(rowlast.get(i)))type="整型";
            else if(utils.isDouble(rowlast.get(i)))type="浮点型";
            else if(utils.isDate(rowlast.get(i)))type="时间型";
            else if(rowlast.get(i).length()<5000)type="字符串";
            else if(rowlast.get(i).length()>=5000)type="文本型";
            sql="insert into dy"+id+TabName+" (字段名称,字段类型,备注,字段长度,小数精度) values('"+row1.get(i)+"','"+type+"','','','')";
            try
            {
                st=conn.createStatement();
                st.executeUpdate(sql);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {

            }
        }
        Dbutil.close(rs, st, conn);
    }
    //xlsx和xls生成字典
    public void CreateExcelDictionary(String TabName,int id,List<Object> row1,List<Object> rowlast) throws SQLException {
        String sql="create table dy"+id+TabName+" (字段名称 varchar(100),字段类型 varchar(20),备注 varchar(100),字段长度 varchar(10),小数精度 varchar(10))";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        String type="";
        for(int i=0;i<row1.size();i++){
            if(utils.isInteger((String)rowlast.get(i)))type="整型";
            else if(utils.isDouble((String)rowlast.get(i)))type="浮点型";
            else if(utils.isDate((String)rowlast.get(i)))type="时间型";
            else if(((String) rowlast.get(i)).length()<5000)type="字符串";
            else if(((String) rowlast.get(i)).length()>=5000)type="文本型";
            sql="insert into dy"+id+TabName+" (字段名称,字段类型,备注,字段长度,小数精度) values('"+row1.get(i)+"','"+type+"','','','')";
            try
            {
                st=conn.createStatement();
                st.executeUpdate(sql);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {

            }
        }
        Dbutil.close(rs, st, conn);
    }
    //删除一个数据集时删除一条数据并删除两个表
    public void DeleteOnHistory(String TabId,String Tabname){
        String sql="drop table data"+TabId+Tabname;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="drop table dy"+TabId+Tabname;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="delete from datas where id ="+TabId;

        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
    }
    //获取字典内容
    public List<DictionaryBean> GetDictionary(String TabID,String TabName){
        String sql = "select * from  dy"+TabID+TabName;
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<DictionaryBean> list = new ArrayList<>();
        DictionaryBean bean = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                String zdmc=rs.getString("字段名称");
                String zdlx=rs.getString("字段类型");
                String beizhu=rs.getString("备注");
                String zdcd=rs.getString("字段长度");
                String xsjd=rs.getString("小数精度");
                bean=new DictionaryBean(zdmc,zdlx,beizhu,zdcd,xsjd);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        return list;
    }
    //修改字典的某一字段
    public void ChangeDictionaryOne(String TabID,String TabName,String ziduan,String zdmc,String value){
        String sql="update dy"+TabID+TabName+" set "+ziduan+"='"+value+"' where 字段名称='"+zdmc+"'";
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
    }
    //删除字典中的某一个字段
    public void DeleteOneZiDuan(String zdmc,String TabID,String TabName,String showid){
        String sql="delete from dy"+TabID+TabName+" where 字段名称 ='"+zdmc+"'";
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }


    }
    //数据备份
    public void DataBackUp(String UserID,String OldDataName,int TabID) throws SQLException {
        String TabName=utils.DelHouZhui(OldDataName);
        String sql="create table bf"+TabID+TabName+" like data"+TabID+TabName;
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        sql="insert into beifen (userid,dataname,time,yuanid) values('"+UserID+"','"+OldDataName+"','"+utils.GetCurrentTime()+"','"+TabID+"')";
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        sql="insert into bf"+TabID+TabName +" select * from data"+TabID+TabName;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        Dbutil.close(st, conn);
    }
    //获得备份表的表格
    public List<BackUpBean> showDataBackUp(String userId){
        String sql = "select * from beifen where userid="+userId;
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<BackUpBean> list = new ArrayList<>();
        BackUpBean bean = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id=rs.getInt("id");
                int userid=rs.getInt("userid");
                String dataname=rs.getString("dataname");
                String time=rs.getString("time");
                String state=rs.getString("state");
                String beizhu=rs.getString("beizhu");
                int yuanid=rs.getInt("yuanid");
                bean=new BackUpBean(id,userid,dataname,time,state,beizhu,yuanid);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        return list;
    }
    //删除一个备份数据集
    public void DeleteOneBackUp(String BFID,String BFDataname){
        String TabName=utils.DelHouZhui(BFDataname);
        String sql="delete from beifen where id ="+BFID;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="drop table bf"+BFDataname+TabName;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        Dbutil.close(st, conn);
    }
    //修改备份数据集备注
    public void ChangeBackUpBeizhu(String BFID,String value){
        String sql="update beifen set beizhu='"+value+"' where id="+BFID;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        Dbutil.close(st, conn);
    }
    //将备份数据集返回操作数据集
    public void DataBackUpToData(String NowTime,String BFID,String BFName,String Yuanid,String userID,List<String> clname) throws SQLException {
        String sql="insert into datas (userid,dataname,time,state) values('"+userID+"','"+BFName+"','"+NowTime+"','"+"上传完成"+"')";
        Connection conn=Dbutil.getConnection();
        PreparedStatement pt = conn.prepareStatement(sql);
        Statement st=null;
        ResultSet rs=null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        int nowid=(new Dao()).getThisID(userID,NowTime);
        BFName=utils.DelHouZhui(BFName);
        sql="create table data"+nowid+BFName+" like bf"+Yuanid+BFName;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        sql="insert into data"+nowid+BFName +" select * from bf"+Yuanid+BFName;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        List<String> rowlast=new ArrayList<>();
        String now="";
        sql="select * from bf"+Yuanid+BFName+" limit 100";
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                List<String> midList=new ArrayList<>();
                for(int i=0;i<clname.size();i++){
                    now=rs.getString(clname.get(i));
                    midList.add(now);
                }
                rowlast=midList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        List<String> row1=new ArrayList<>();
        sql="select * from bf"+Yuanid+BFName+" limit 1";
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {

                for(int i=0;i<clname.size();i++){
                    now=rs.getString(clname.get(i));
                    row1.add(now);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        sql="create table dy"+nowid+BFName+" (字段名称 varchar(100),字段类型 varchar(20),备注 varchar(100),字段长度 varchar(10),小数精度 varchar(10))";
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        String type="";
        for(int i=0;i<rowlast.size();i++){
            if(utils.isInteger(rowlast.get(i)))type="整型";
            else if(utils.isDouble(rowlast.get(i)))type="浮点型";
            else if(utils.isDate(rowlast.get(i)))type="时间型";
            else if(( rowlast.get(i)).length()<5000)type="字符串";
            else if(( rowlast.get(i)).length()>=5000)type="文本型";
            sql="insert into dy"+nowid+BFName+" (字段名称,字段类型,备注,字段长度,小数精度) values('"+row1.get(i)+"','"+type+"','','','')";
            try
            {
                st=conn.createStatement();
                st.executeUpdate(sql);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {

            }
        }
        Dbutil.close(rs, st, conn);
    }
    //获得数据表列名
    public static List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = Dbutil.getConnection();
        PreparedStatement pStemt = null;
        String tableSql = "select * from " + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    Dbutil.close(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        Dbutil.close(conn);
        return columnNames;
    }
    //数据清洗数据集表格
    public List<HistoryTabBean> DataCleanTab(String userID){
        String sql = "select * from datas where userid="+userID;
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<HistoryTabBean> list = new ArrayList<>();
        HistoryTabBean bean = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                int id=rs.getInt("id");
                String dataname=rs.getString("dataname");
                String beizhu=rs.getString("beizhu");
                bean=new HistoryTabBean(id,dataname,beizhu);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        return list;
    }
    //去重时的到数据集列
    public List<String> BaseIDGetCl(String Tabid,String TabName,List<String> clname){
        String sql = "select * from data"+Tabid+TabName+" limit 1";
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<String> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                for(int i=0;i<clname.size();i++){
                    list.add(rs.getString(clname.get(i)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Dbutil.close(st, conn);
        }
        return list;
    }
    //根据得到的列进行去重
    public void QUCHONG(String Tabid,String TabName,int cl,int clsize){
        //定义一个集合用来存含有重复的值
        List<String> chongfulist=new ArrayList<>();
        String sql = "select cl"+cl+" from data"+Tabid+TabName+" Group By cl"+cl+" Having Count(*)>1";
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        List<String> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                chongfulist.add(rs.getString("cl"+cl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        //获得含有重复值的列读取全部，并根据最多前5个字段删除一条信息
        for(int i=0;i<chongfulist.size();i++){

            if(clsize<5){
                List<List<String>> chongfu=new ArrayList<>();
                sql="select cl0 from data"+Tabid+TabName+"where cl"+cl+" ='"+chongfulist.get(i)+"'";
                try {
                    st = conn.createStatement();
                    st.executeQuery(sql);
                    rs = st.executeQuery(sql);
                    while (rs.next()) {
                        List<String> slist=new ArrayList<>();
                        slist.add(rs.getString("cl0"));
                        chongfu.add(slist);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {

                }
                for(int j=1;i<chongfu.size();j++){
                    sql="delete from data"+Tabid+TabName+ " where cl0 ='"+chongfu.get(j).get(0)+"'";
                    try {
                        st=conn.createStatement();
                        st.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    finally{
                    }
                }
            }
            else if(clsize>=5){
                List<List<String>> chongfu=new ArrayList<>();
                sql="select cl0,cl1,cl2,cl3,cl4 from data"+Tabid+TabName+" where cl"+cl+" ='"+chongfulist.get(i)+"'";
                System.out.println("SQL:"+sql);
                try {
                    st = conn.createStatement();
                    st.executeQuery(sql);
                    rs = st.executeQuery(sql);
                    while (rs.next()) {
                        List<String> slist=new ArrayList<>();
                        slist.add(rs.getString("cl0"));
                        slist.add(rs.getString("cl1"));
                        slist.add(rs.getString("cl2"));
                        slist.add(rs.getString("cl3"));
                        slist.add(rs.getString("cl4"));
                        chongfu.add(slist);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {

                }
                for(int j=1;i<chongfu.size();j++){
                    sql="delete from data"+Tabid+TabName+" where cl0 ='"+chongfu.get(j).get(0)+"' and cl1='"+chongfu.get(j).get(1)+"' and cl2='"+chongfu.get(j).get(2)+"' and cl3='"+chongfu.get(j).get(3)+"' and cl4='"+chongfu.get(j).get(4)+"'";
                    System.out.println("SQL1"+sql);
                    try {
                        st=conn.createStatement();
                        st.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    finally{
                    }
                }
            }
        }
    }
    //获得缺失字段的含有缺失的数据
    public List<List<String>> showExcelQueShi(String TabID,int clsize,String cl){
        String tabname=getDataTabName(TabID);
        List<List<String>> bigList=new ArrayList<>();
        String sql="select * from data"+TabID+tabname+" limit 1";

        Connection conn =Dbutil.getConnection();
        Statement st=null;
        ResultSet rs=null;
        try {
            st=conn.createStatement();
            st.executeQuery(sql);
            rs=st.executeQuery(sql);
            while(rs.next()) {
                List<String> list=new ArrayList<>();
                for(int i=0;i<clsize;i++){
                    list.add(rs.getString("cl"+i));
                }
                bigList.add(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="select * from data"+TabID+tabname+" where cl"+cl+"=''";
        try {
            st=conn.createStatement();
            st.executeQuery(sql);
            rs=st.executeQuery(sql);
            while(rs.next()) {
                List<String> list=new ArrayList<>();
                for(int i=0;i<clsize;i++){
                    list.add(rs.getString("cl"+i));
                }
                bigList.add(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        System.out.println("biglist的数量:"+bigList.size());
        return bigList;

    }
    //修改缺失值的某个字段
    public void ChangeOneQueShi(String TabID,String TabName,String cl0,String changecl,String value){
        String sql="update data"+TabID+TabName+" set "+changecl+"='"+value+"' where cl0='"+cl0+"'";
        System.out.println("修改缺失值字段的SQL"+sql);
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
    }
    //删除无意义的数据
    public int DelNoMeaing(String TabID,String TabName,String cl,String value){
        //创建临时表
        String sql = "create table temporary"+TabID+TabName+" like data"+TabID+TabName;
        Connection conn = Dbutil.getConnection();
        Statement st = null;
        ResultSet rs = null;
        try
        {
            st=conn.createStatement();
            st.executeUpdate(sql);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
        sql = "insert into temporary"+TabID+TabName +" select * from data"+TabID+TabName+" limit 1";
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        int count=0;
        sql = "select count(*) from  data"+TabID+TabName+" where cl"+cl+" <"+value;
        try {
            st = conn.createStatement();
            st.executeQuery(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                count=rs.getInt("count(*)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
        sql="delete from data"+TabID+TabName+" where cl"+cl+" <"+value;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql = "insert into temporary"+TabID+TabName +" select * from data"+TabID+TabName;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="drop table data"+TabID+TabName;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{

        }
        sql="alter table temporary"+TabID+TabName+" rename to data"+TabID+TabName;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        return count-1;
    }
    //字符串拼接
    public void pinjie(String TabID,String TabName,String cl1,String cl2,String cl3){
        String sql="update data"+TabID+TabName+" set cl"+cl1+"= cl"+cl2+"+cl"+cl3;
        Connection conn =Dbutil.getConnection();
        Statement st=null;
        try {
            st=conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            Dbutil.close(st, conn);
        }
        System.out.println("PINJIESQL:"+sql);
    }
}
