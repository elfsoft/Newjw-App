package cn.elfsoft.assist.modle;

import java.util.List;

public class JsonModel {
	private String errorMsg;
	private LeaveModel leaveModel;
	private LoginModel loginModel;
	private List<Rows> rows;
	private List<TeacherList> teacherList;
	private boolean success;
	private int total;
	
	
	public LoginModel getLoginModel() {
		return loginModel;
	}
	public void setLoginModel(LoginModel loginModel) {
		this.loginModel = loginModel;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public LeaveModel getLeaveModel() {
		return leaveModel;
	}
	public void setLeaveModel(LeaveModel leaveModel) {
		this.leaveModel = leaveModel;
	}
	public List<Rows> getRows() {
		return rows;
	}
	public void setRows(List<Rows> rows) {
		this.rows = rows;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<TeacherList> getTeacherList() {
		return teacherList;
	}
	public void setTeacherList(List<TeacherList> teacherList) {
		this.teacherList = teacherList;
	}
	
	
}
