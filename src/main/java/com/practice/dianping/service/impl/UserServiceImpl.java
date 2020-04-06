package com.practice.dianping.service.impl;

import com.practice.dianping.dal.UserModelMapper;
import com.practice.dianping.model.UserModel;
import com.practice.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserModelMapper userModelMapper;

  @Override
  public UserModel getUser(Integer id) {
    return userModelMapper.selectByPrimaryKey(id);
  }
}
