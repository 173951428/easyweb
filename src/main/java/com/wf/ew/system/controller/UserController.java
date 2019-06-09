package com.wf.ew.system.controller;

import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.exception.ParameterException;
import com.wf.ew.common.shiro.EndecryptUtil;
import com.wf.ew.common.utils.StringUtil;
import com.wf.ew.common.utils.excelUtils.ExcelUtils;
import com.wf.ew.system.Dto.DeptDto;
import com.wf.ew.system.Dto.TeamDto;
import com.wf.ew.system.Dto.UserDto;
import com.wf.ew.system.model.Dept;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.DeptService;
import com.wf.ew.system.service.RoleService;
import com.wf.ew.system.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.io.*;

/**
 * 用户管理
 */
@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
	
	 private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DeptService deptService; 
    
    


    @RequiresPermissions("user:view")
    @RequestMapping
    public String user(Model model) {
        List<Role> roles = roleService.list(false);
        model.addAttribute("roles", roles);
        return "system/user.html";
    }
    
    

    @RequestMapping("/editForm")
    public String addUser(Model model) {
        List<Role> roles = roleService.list(false);
        model.addAttribute("roles", roles);
        return "system/user_form.html";
    }

    /**
     * 查询用户列表
     */
    @RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/list")
    public PageResult<User> list(Integer page, Integer limit, String searchKey, String searchValue) {
        if (page == null) {
            page = 0;
            limit = 0;
        }
        if (StringUtil.isBlank(searchValue)) {
            searchKey = null;
        }
        return userService.list(page, limit, true, searchKey, searchValue);
    }
    
    

   
    /**
     * 添加用户
     **/
    @RequiresPermissions("user:add")
    @ResponseBody
    @RequestMapping("/add")
    public JsonResult add(User user, String roleId) {
        user.setRoles(getRoles(roleId));
        user.setPassword("123456");
        if (userService.add(user)) {
            return JsonResult.ok("添加成功");
        } else {
            return JsonResult.error("添加失败");
        }
    }

    /**
     * 修改用户
     **/
    @RequiresPermissions("user:edit")
    @ResponseBody
    @RequestMapping("/update")
    public JsonResult update(User user, String roleId) {
        user.setRoles(getRoles(roleId));
        if (userService.update(user)) {
            return JsonResult.ok("修改成功");
        } else {
            return JsonResult.error("修改失败");
        }
    }

    private List<Role> getRoles(String roleStr) {
        List<Role> roles = new ArrayList<>();
        String[] split = roleStr.split(",");
        for (String t : split) {
            if (t.equals("1")) {
                throw new ParameterException("不能添加超级管理员");
            }
            roles.add(new Role(Integer.parseInt(t)));
        }
        return roles;
    }

    /**
     * 修改用户状态
     **/
    @RequiresPermissions("user:delete")
    @ResponseBody
    @RequestMapping("/updateState")
    public JsonResult updateState(Integer userId, Integer state) {
        if (userService.updateState(userId, state)) {
            return JsonResult.ok();
        } else {
            return JsonResult.error();
        }
    }

    /**
     * 修改自己密码
     **/
    @ResponseBody
    @RequestMapping("/updatePsw")
    public JsonResult updatePsw(String oldPsw, String newPsw) {
        if ("admin".equals(getLoginUser().getUsername())) {
            return JsonResult.error("演示账号admin关闭该功能");
        }
        String finalSecret = EndecryptUtil.encrytMd5(oldPsw, getLoginUserName(), 3);
        if (!finalSecret.equals(getLoginUser().getPassword())) {
            return JsonResult.error("原密码输入不正确");
        }
        if (userService.updatePsw(getLoginUserId(), getLoginUserName(), newPsw)) {
            return JsonResult.ok("修改成功");
        } else {
            return JsonResult.error("修改失败");
        }
    }

    /**
     * 重置密码
     **/
    @RequiresPermissions("user:edit")
    @ResponseBody
    @RequestMapping("/restPsw")
    public JsonResult resetPsw(Integer userId) {
        User byId = userService.getById(userId);
        if (userService.updatePsw(userId, byId.getUsername(), "123456")) {
            return JsonResult.ok("重置成功");
        } else {
            return JsonResult.error("重置失败");
        }
    }
    
    /**
     * excel批量上传user
     * @param file
     * @param User 
     * @return
     * @throws Exception 
     */
    @ResponseBody
    @RequestMapping("/uploadUserExcel")
    public JsonResult uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	 try {
			InputStream is = file.getInputStream();
			 List<UserDto> userDtos= ExcelUtils.readExcel(is,new UserDto());
			 List<User> users=new ArrayList<User>();
			 for (UserDto userDto : userDtos) {
				 User user=new User();
				 BeanUtils.copyProperties(userDto,user);// 把userDto 复制到user
				 user.setRoles(getRoles("3"));// 默认权限为普通用户
				 user.setPassword("123456");
				 users.add(user);
			 }
			
			 if(null!=users&&users.size()>0) {
				for (User user : users) {
					if(null!=userService.getByUsername(user.getUsername())) {
						 return JsonResult.ok(2,"上传失败,用户存在！");
					}else {
						userService.add(user);
					}
				} 
			 }
			 
			 return JsonResult.ok(0,"上传成功！");
		   	 
		} catch (Exception e) {
			logger.error("excel批量导入用户失败！");
			 return JsonResult.error(1, "上传失败！");
		}
    	
		
    }

    @RequestMapping("/downloadUser")
    @ResponseBody
    public JsonResult downloadUser(HttpServletRequest request) throws IOException {
    	 String searchKey=request.getParameter("searchKey");
    	 String searchValue=request.getParameter("searchValue");
    	 List<User>  users=userService.selectByParam(searchKey,searchValue);
    	 String outpath="D:\\excel\\user.xlsx";
    	 ExcelUtils.writeExcel(users, outpath);
    	 return JsonResult.ok(0,"下载成功！");
    	
    }
    
    // 导出用户excel
    @RequestMapping(value="/downloadUser1",produces = {"application/vnd.ms-excel;charset=UTF-8"})
    public  ResponseEntity<byte[]>  downloadUser1(HttpServletRequest request) throws IOException {
    	
    	 String searchKey=request.getParameter("searchKey");
    	 String searchValue=request.getParameter("searchValue");
    	 List<User>  users=userService.selectByParam(searchKey,searchValue);
    	 String fileName="用户表";
    	 List<Map<String,Object>> list=userService.createExcelRecord(users);
    	 String columnNames[]={"账号","权限","性别","电话","邮件","创建时间"};//列名
    	  //map中的key
    	 String keys[] = {"username","nickName","sex","phone","email","createTime"};
    	 ByteArrayOutputStream os = new ByteArrayOutputStream();
    	 ExcelUtils.createWorkBook(list,keys,columnNames).write(os);
         byte[] content = os.toByteArray();
         HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.add("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName+".xlsx", "UTF-8")); //防止中文乱码
         ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(content,httpHeaders,HttpStatus.OK);
         return responseEntity;

		
    }
    
    
    

    
    
}
