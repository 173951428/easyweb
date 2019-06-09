package com.wf.ew.system.service;

import java.util.List;

import com.wf.ew.system.model.Dept;


public interface DeptService {
	  List<Dept> list();
	  public List<Dept> listByParam(String  column,String param);
}
