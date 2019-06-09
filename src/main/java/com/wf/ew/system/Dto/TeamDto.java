package com.wf.ew.system.Dto;
import java.util.*;
//二级团队Dto
public class TeamDto {
  private String teamName;//一级团队名字
  private List<String> secondTeam;//二级团队的名字集合
public String getTeamName() {
	return teamName;
}
public void setTeamName(String teamName) {
	this.teamName = teamName;
}
public List<String> getSecondTeam() {
	return secondTeam;
}
public void setSecondTeam(List<String> secondTeam) {
	this.secondTeam = secondTeam;
}
}
