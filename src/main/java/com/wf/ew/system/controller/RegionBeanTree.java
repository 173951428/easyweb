package com.wf.ew.system.controller;

import java.util.List;
public class RegionBeanTree {
	   private String code ;   //部门代码
	    private String pid ;   //父部门名字
	    private String label ; //部门名字
	    private List<RegionBeanTree> children;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getPid() {
			return pid;
		}
		public void setPid(String pid) {
			this.pid = pid;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public List<RegionBeanTree> getChildren() {
			return children;
		}
		public void setChildren(List<RegionBeanTree> children) {
			this.children = children;
		}
}
