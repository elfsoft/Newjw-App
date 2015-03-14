package cn.elfsoft.assist.logic;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.elfsoft.assist.modle.AttendsModel;
import cn.elfsoft.assist.modle.JsonModel;

public class InSchoolHttpCent extends HttpCent {
    protected String httpUrl = "http://stu.neusoft.edu.cn/qes/";
    private String sessionid;

    public boolean login(String username, String password) throws Exception, Exception {
        String path = "portal/LoginAction.action?method=login&loginModel.no=" + username + "&loginModel.password=" + password
                + "&loginModel.role=2";
        String result = new String(readInputStream(request(path)), "utf-8");
        System.out.println(result);
        JsonModel model = JSON.parseObject(result, JsonModel.class);
        return model.isSuccess();
    }

    public boolean checkLogin() throws Exception {
        String path = "pages/leave/QueryoneleaveAction.action?method=queryOneLeave&leaveModel.beginRowNum=0&leaveModel.endRowNum=31";
        String result = new String(readInputStream(request(path)), "utf-8");
        JsonModel model = JSON.parseObject(result, JsonModel.class);
        if (model.getErrorMsg() != null) {
            return false;
        }
        return true;
    }

    public JsonModel getTeacherInfo(String sid) throws UnsupportedEncodingException, Exception {
        String path = "pages/leave/OnlineApplyAction.action?method=queryTeachers&leaveModel.studentno=" + sid;
        String result = new String(readInputStream(request(path)), "utf-8");
        JsonModel model = JSON.parseObject(result, JsonModel.class);
        return model;
    }

    public JsonModel getLeaveInfo() throws Exception {
        String path = "pages/leave/QueryoneleaveAction.action?method=queryOneLeave&leaveModel.beginRowNum=0&leaveModel.endRowNum=31";
        String result = new String(readInputStream(request(path)), "utf-8");
        JsonModel model = JSON.parseObject(result, JsonModel.class);
        return model;
    }

    public void addLeaveInfo(String startdate, String enddate, String startsection, String endsection, String teachername,
                             String teacherno, String telphone, String reason) throws UnsupportedEncodingException, Exception {
        reason = new String(reason.getBytes("utf-8"), "iso8859-1");
        teachername = new String(teachername.getBytes("utf-8"), "iso8859-1");
        String path = "pages/leave/OnlineApplyAction.action?method=addLeave&teacherno=" + teacherno + "&reason=" + reason
                + "&afltype=2&startsection=" + startsection + "&endsection=" + endsection + "&startdate=" + startdate + "&enddate="
                + enddate + "&telephone=" + telphone + "&teachername=" + teachername;
        String result = new String(readInputStream(request(path)), "utf-8");
    }

    public AttendsModel getKaoQinInfo() throws UnsupportedEncodingException, Exception {
        String path = "pages/student/StuAttendAction.action?method=queryStuAttend&condition.beginRowNum=0&condition.endRowNum=31";
        String result = new String(readInputStream(request(path)), "utf-8");
        System.out.println(result);
        return JSON.parseObject(result, AttendsModel.class);

    }

    public boolean deleteLeaveInfo(String id) throws UnsupportedEncodingException, Exception {
        String path = "pages/leave/QueryoneleaveAction.action?method=delLeaveinfo&aflid=" + id;
        String result = new String(readInputStream(request(path)), "utf-8");
        JsonModel model = JSON.parseObject(result, JsonModel.class);
        return model.isSuccess();
    }

    public String getSessionId() throws Exception {
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            throw new Exception("��ȡ��֤�����");
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Referer", httpUrl);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (MSIE 9.0; Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", "4629");
        sessionid = conn.getHeaderField("Set-Cookie").split("=")[1].split(";")[0];
        return sessionid;
    }

    protected InputStream request2(String path) throws Exception {
        URL url = new URL(httpUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(2 * 1000);
        conn.setReadTimeout(2 * 1000);
        conn.setRequestProperty("Referer", httpUrl + path);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (MSIE 9.0; Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
        conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionid);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", "4629");
        InputStream in = conn.getInputStream();
        if (in == null)
            throw new Exception("����ʧ��");
        return in;
    }

    protected InputStream request(String path) throws Exception {
        for (int i = 0; i < 5; i++) {
            try {
                return request2(path);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

}
