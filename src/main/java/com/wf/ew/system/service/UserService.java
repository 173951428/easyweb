package com.wf.ew.system.service;

import java.util.List;
import java.util.*;
import com.wf.ew.common.PageResult;
import com.wf.ew.common.exception.BusinessException;
import com.wf.ew.common.exception.ParameterException;
import com.wf.ew.system.model.User;

public interface UserService {

    User getByUsername(String username);

    PageResult<User> list(int pageNum, int pageSize, boolean showDelete, String searchKey, String searchValue);

    User getById(Integer userId);

    boolean add(User user) throws BusinessException;

    boolean update(User user);

    boolean updateState(Integer userId, int state) throws ParameterException;

    boolean updatePsw(Integer userId, String username, String newPsw);

    boolean delete(Integer userId);
    
    List<User> selectByParam(String searchKey, String searchValue);
    
    public List<Map<String, Object>> createExcelRecord(List<User> users); 
    
    public Integer selectByEmail(String email);

}
