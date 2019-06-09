package com.wf.ew.system.model;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("dept")
public class Dept {
  private int id;
  private String deptName;// 部门名字
  private String deptCode;//部门代码
  private String parentDept;//父部门
  public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getDeptName() {
	return deptName;
}
public void setDeptName(String deptName) {
	this.deptName = deptName;
}
public String getDeptCode() {
	return deptCode;
}
public void setDeptCode(String deptCode) {
	this.deptCode = deptCode;
}
public String getParentDept() {
	return parentDept;
}
public void setParentDept(String parentDept) {
	this.parentDept = parentDept;
}
public String getLevel() {
	return level;
}
public void setLevel(String level) {
	this.level = level;
}
private String level;//等级
  
  
}
