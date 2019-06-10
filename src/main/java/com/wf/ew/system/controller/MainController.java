package com.wf.ew.system.controller;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sun.mail.util.MailSSLSocketFactory;
import com.wf.captcha.utils.CaptchaUtil;
import com.wf.ew.common.BaseController;
import com.wf.ew.common.JsonResult;
import com.wf.ew.common.exception.ParameterException;
import com.wf.ew.common.utils.StringUtil;
import com.wf.ew.common.utils.TimeUtil;
import com.wf.ew.common.utils.UserAgentGetter;
import com.wf.ew.system.Dto.DeptDto;
import com.wf.ew.system.model.Authorities;
import com.wf.ew.system.model.Dept;
import com.wf.ew.system.model.LoginRecord;
import com.wf.ew.system.model.Role;
import com.wf.ew.system.model.User;
import com.wf.ew.system.service.AuthoritiesService;
import com.wf.ew.system.service.DeptService;
import com.wf.ew.system.service.LoginRecordService;
import com.wf.ew.system.service.UserService;

/**
 * MainController
 */
@Controller
public class MainController extends BaseController implements ErrorController {
    @Autowired
    private AuthoritiesService authoritiesService;
    @Autowired
    private LoginRecordService loginRecordService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private DeptService deptService; 

    /**
     * 主页
     */
    @RequestMapping({"/", "/index"})
    public String index(Model model) {
        List<Authorities> authorities = authoritiesService.listByUserId(getLoginUserId());
        List<Map<String, Object>> menuTree = getMenuTree(authorities, -1);
        model.addAttribute("menus", menuTree);
        model.addAttribute("login_user", getLoginUser());
        return "index.html";
    }

    /**
     * 登录页
     */
  @GetMapping("/login")
    public String login() {
        if (getLoginUser() != null) {
            return "redirect:index";
        }
        return "login.html";
    }
    
  
    /**
     * 登录
     */
    @ResponseBody
    @PostMapping("/login")
    public JsonResult doLogin(String username, String password, String code, HttpServletRequest request) {
        if (StringUtil.isBlank(username, password)) {
            return JsonResult.error("账号密码不能为空");
        }
        if (!CaptchaUtil.ver(code, request)) {
            CaptchaUtil.clear(request);
            return JsonResult.error("验证码不正确");
        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            SecurityUtils.getSubject().login(token);
            addLoginRecord(getLoginUserId(), request);
            return JsonResult.ok("登录成功");
        } catch (IncorrectCredentialsException ice) {
            return JsonResult.error("密码错误");
        } catch (UnknownAccountException uae) {
            return JsonResult.error("账号不存在");
        } catch (LockedAccountException e) {
            return JsonResult.error("账号被锁定");
        } catch (ExcessiveAttemptsException eae) {
            return JsonResult.error("操作频繁，请稍后再试");
        }
    }
    
    /**
               * 注册方法
     * @param email   	 邮箱
     * @param vercode 	 邮箱验证码
     * @param username  用户名
     * @param password  密码
     * @param passwordTwo 二次密码
     * @param code 图片验证码
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/doReg")
    public JsonResult doReg(String email,String vercode,String username, String password, String passwordTwo, String code, HttpServletRequest request) {
    	System.out.println("得到的验证码是："+vercode);
    	String sessionVerCode=(String) request.getSession().getAttribute("checkCode");
    	String vcodeTimeArray[] = sessionVerCode.split("#");
    	if(vcodeTimeArray[0].equals(vercode)) {
    		 boolean flag=TimeUtil.cmpTime(vcodeTimeArray[1]);
    		 if(flag==false) {
    			 return JsonResult.error("邮箱验证码超时");
    		 }
    	}else {
    		return JsonResult.error("邮箱验证不正确");
    	}
    	if(!password.equals(passwordTwo)) {
    		return JsonResult.error("两次密码输入不一致");
    	}
        if (StringUtil.isBlank(username, password)) {
            return JsonResult.error("账号密码不能为空");
        }
        if (!CaptchaUtil.ver(code, request)) {
            CaptchaUtil.clear(request);
            return JsonResult.error("验证码不正确");
        }
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickName("管理员");
        user.setSex("男");
        user.setEmail(email);
        user.setRoles(getRoles("3"));// 默认权限为普通用户
        userService.add(user);
        return JsonResult.ok("注册成功");
        
    } 
    
 /**
  * 忘记密码下一步
  * @param email
  * @param vercode
  * @param request
  * @return
  */
    @ResponseBody
    @PostMapping("/doForgotNext")
    public JsonResult doForgotNext(String email,String vercode,HttpServletRequest request,Model model) {
    	System.out.println("得到的验证码是："+vercode+",,得到的邮箱是:"+email);
    	String sessionVerCode=(String) request.getSession().getAttribute("forgotCode");
    	String vcodeTimeArray[] = sessionVerCode.split("#");
    	if(vcodeTimeArray[0].equals(vercode)) {
    		 boolean flag=TimeUtil.cmpTime(vcodeTimeArray[1]);
    		 if(flag==false) {
    			 return JsonResult.error("邮箱验证码超时");
    		 }
    	}else {
    		return JsonResult.error("邮箱验证不正确");
    	}
    	request.getSession().setAttribute("changeEmail", email);
        return JsonResult.ok();
        
    }
    /**
     * 图形验证码，用assets开头可以排除shiro拦截
     */
    @RequestMapping("/assets/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        try {
        	//CaptchaUtil.outPng(request, response); //设置输出图片验证码，非动态
        	CaptchaUtil.outPng(1, request, response);
           // CaptchaUtil.out(request, response); //输出动态验证码，看的头晕
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 找回密码第二步，更改密码
     * @return
     */
    @RequestMapping("/change")
    public String change(HttpServletRequest request,Model model) {
    	
    	 String changeEmail=(String)request.getSession().getAttribute("changeEmail");
    	 if(StringUtil.isBlank(changeEmail)) {
    		 return "forgot.html";
    	 }
    	  model.addAttribute("changeEmail", changeEmail);
    	 return "change.html";
    }
    
    @ResponseBody
    @PostMapping("/changePassword")
    public JsonResult changePassword(String email) {
    	System.out.println("得到需要修改的email为:"+email);
		return null;
        
    }
    
    /**
     * 
     * @return
     */
    @RequestMapping("/reg")
    public String reg() {
    	 return "reg.html";
    }
    
    // ztree测试页面
    @RequestMapping("/demo")
    public String demo() {
    	 return "demo.html";
    }
    
    //找回密码step1
    @RequestMapping("forgot")
    public String forgot() {
    	return "forgot.html";
    }
    
    @ResponseBody
    @RequestMapping("/listDept")
    public String list() {
        List<Dept> deptList=deptService.list();
        List<DeptDto> deptDeptDtoList=new ArrayList<DeptDto>();
        for (Dept dept : deptList) {
			DeptDto dto=new DeptDto();
			dto.setId(dept.getDeptCode()); //id 转化为code
			dto.setName(dept.getDeptName()); //name 转化为name
			dto.setpId(dept.getParentDept()); //pid转为父部门
			deptDeptDtoList.add(dto);
		}
        System.out.println(JSONArray.toJSONString(deptDeptDtoList));
        return JSONArray.toJSONString(deptDeptDtoList);
    }
    
    /**
                * 获取邮箱验验证码
     */
    @ResponseBody
    @PostMapping("/getEmailCode")
    public JsonResult getEmail(HttpServletRequest request) throws Exception,GeneralSecurityException, MessagingException {
			String  receiveMailAccount=request.getParameter("email"); //要发送的邮箱
			Properties props = new Properties();
	        // 开启debug调试
	        props.setProperty("mail.debug", "true");
	        // 发送服务器需要身份验证
	        props.setProperty("mail.smtp.auth", "true");
	        // 设置邮件服务器主机名
	        props.setProperty("mail.host", "smtp.qq.com");
	        // 发送邮件协议名称
	        props.setProperty("mail.transport.protocol", "smtp");
	     
	        MailSSLSocketFactory sf = new MailSSLSocketFactory();
	        sf.setTrustAllHosts(true);
	        props.put("mail.smtp.ssl.enable", "true");
	        props.put("mail.smtp.ssl.socketFactory", sf);
	        Session session = Session.getInstance(props);
	        Message msg = new MimeMessage(session);
	        msg.setSubject(" the springBoot project verification code"); //邮件标题
	        StringBuilder builder = new StringBuilder();
	        String checkCode= String.valueOf((new Random().nextInt(899999) + 100000));
	        HttpSession sysSession= request.getSession();
	        String nowTime = TimeUtil.getTime();
	        sysSession.setAttribute("checkCode",checkCode+"#"+nowTime);
	        System.out.println("checkCode的值为："+sysSession.getAttribute("checkCode"));
	        builder.append("your verification code:");
	        builder.append(" ");
	        builder.append(checkCode);
	        builder.append("\n This verification code is used for registration the project,Please don't tell anyone !");
	        msg.setText(builder.toString());
	        msg.setFrom(new InternetAddress("scootzhao@qq.com"));  //发送人的邮箱
	        Transport transport = session.getTransport();
	        /**
	         * param1  发件人的邮箱协议
	         * 
	         * param2 发件人的邮箱地址
	         * 
	         * param3  POP3/SMTP服务,这时QQ邮件会让我们设置客户端授权码
	         */
	        transport.connect("smtp.qq.com", "scootzhao@qq.com", "cqrepmaasqvrbffc"); 
	        try {
				transport.sendMessage(msg, new Address[] {new InternetAddress(receiveMailAccount)});
			} catch (Exception e) {
				e.printStackTrace();
			}
	        transport.close();
	        return JsonResult.ok(0,"邮件发送成功");
	}
    
    /**
     * 重置密码发送邮件
     * @param request
     * @return
     * @throws Exception
     * @throws GeneralSecurityException
     * @throws MessagingException
     */
    @ResponseBody
    @PostMapping("/getForgotEmailCode")
    public JsonResult getForgotEmailCode(HttpServletRequest request) throws Exception,GeneralSecurityException, MessagingException {
    	String  receiveMailAccount=request.getParameter("email"); //要发送的邮箱
    	if(userService.selectByEmail(receiveMailAccount)==0) {
    		 return JsonResult.ok(1,"该邮箱尚未注册");
    	}else {
    		Properties props = new Properties();
	        // 开启debug调试
	        props.setProperty("mail.debug", "true");
	        // 发送服务器需要身份验证
	        props.setProperty("mail.smtp.auth", "true");
	        // 设置邮件服务器主机名
	        props.setProperty("mail.host", "smtp.qq.com");
	        // 发送邮件协议名称
	        props.setProperty("mail.transport.protocol", "smtp");
	     
	        MailSSLSocketFactory sf = new MailSSLSocketFactory();
	        sf.setTrustAllHosts(true);
	        props.put("mail.smtp.ssl.enable", "true");
	        props.put("mail.smtp.ssl.socketFactory", sf);
	        Session session = Session.getInstance(props);
	        Message msg = new MimeMessage(session);
	        msg.setSubject(" the springBoot project verification code"); //邮件标题
	        StringBuilder builder = new StringBuilder();
	        String checkCode= String.valueOf((new Random().nextInt(899999) + 100000));
	        HttpSession sysSession= request.getSession();
	        String nowTime = TimeUtil.getTime();
	        sysSession.setAttribute("forgotCode",checkCode+"#"+nowTime);
	        System.out.println("checkCode的值为："+sysSession.getAttribute("forgotCode"));
	        builder.append("your verification code:");
	        builder.append(" ");
	        builder.append(checkCode);
	        builder.append("\n This verification code is used for change the password,Please don't tell anyone !");
	        msg.setText(builder.toString());
	        msg.setFrom(new InternetAddress("scootzhao@qq.com"));  //发送人的邮箱
	        Transport transport = session.getTransport();
	        /**
	         * param1  发件人的邮箱协议
	         * 
	         * param2 发件人的邮箱地址
	         * 
	         * param3  POP3/SMTP服务,这时QQ邮件会让我们设置客户端授权码
	         */
	        transport.connect("smtp.qq.com", "scootzhao@qq.com", "cqrepmaasqvrbffc"); 
	        try {
				transport.sendMessage(msg, new Address[] {new InternetAddress(receiveMailAccount)});
			} catch (Exception e) {
				e.printStackTrace();
			}
	        transport.close();
	        return JsonResult.ok(0,"邮件发送成功");
    	}
    	
    	
	
    }
    
    /**
     * iframe页
     */
    @RequestMapping("/iframe")
    public String error(String url, Model model) {
        model.addAttribute("url", url);
        return "tpl/iframe.html";
    }

    /**
     * 错误页
     */
    @RequestMapping("/error")
    public String error(String code) {
        if ("403".equals(code)) {
            return "error/403.html";
        }
        return "error/404.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * 递归转化树形菜单
     */
    private List<Map<String, Object>> getMenuTree(List<Authorities> authorities, Integer parentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < authorities.size(); i++) {
            Authorities temp = authorities.get(i);
            if (temp.getIsMenu() == 0 && parentId == temp.getParentId()) {
                Map<String, Object> map = new HashMap<>();
                map.put("menuName", temp.getAuthorityName());
                map.put("menuIcon", temp.getMenuIcon());
                map.put("menuUrl", StringUtil.isBlank(temp.getMenuUrl()) ? "javascript:;" : temp.getMenuUrl());
                map.put("subMenus", getMenuTree(authorities, authorities.get(i).getAuthorityId()));
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 添加登录日志
     */
    private void addLoginRecord(Integer userId, HttpServletRequest request) {
        UserAgentGetter agentGetter = new UserAgentGetter(request);
        // 添加到登录日志
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUserId(userId);
        loginRecord.setOsName(agentGetter.getOS());
        loginRecord.setDevice(agentGetter.getDevice());
        loginRecord.setBrowserType(agentGetter.getBrowser());
        loginRecord.setIpAddress(agentGetter.getIpAddr());
        loginRecordService.add(loginRecord);
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

}
