package com.wf.ew.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.wf.ew.system.dao.DeptMapper;
import com.wf.ew.system.model.Dept;
import com.wf.ew.system.service.DeptService;
@Service
public class DeptServiceImpl implements  DeptService{
	 @Autowired
	    private DeptMapper deptMapper;
	 
	@Override
	public List<Dept> list() {
		 Wrapper wrapper = new EntityWrapper();
		 
		// TODO Auto-generated method stub
		return deptMapper.selectList(wrapper.orderBy("level", true));
	}
	/**
	 * 
	 * @param column 列名
	 * @param param  参数
	 * @return
	 */
	public List<Dept> listByParam(String  column,String param){
		return deptMapper.selectList(new EntityWrapper().eq(column, param));
	}

}
