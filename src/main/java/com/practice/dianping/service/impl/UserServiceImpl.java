package com.practice.dianping.service.impl;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.dal.UserModelMapper;
import com.practice.dianping.model.UserModel;
import com.practice.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

  @Resource
  private UserModelMapper userModelMapper;

  @Override
  public UserModel getUser(Integer id) {
    return userModelMapper.selectByPrimaryKey(id);
  }

  @Override
  @Transactional
  public UserModel register(UserModel registerUser) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    registerUser.setCreatedAt(new Date());
    registerUser.setUpdatedAt(new Date());
    registerUser.setPassword(encodeByMd5(registerUser.getPassword()));
    try{
      userModelMapper.insertSelective(registerUser);
    }catch (DuplicateKeyException ex){
      throw new BusinessException(EmBusinessError.REGISTER_DUP_FAIL);
    }
    return getUser(registerUser.getId());
  }

  @Override
  public UserModel login(String telphone, String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    UserModel userModel = userModelMapper.selectByTelphoneAndPassword(telphone, encodeByMd5(password));
    if (userModel==null){
      throw new BusinessException(EmBusinessError.LOGIN_FAIL);
    }
    return userModel;
  }

  @Override
  public Integer countAllUser() {
    return userModelMapper.countAllUser();
  }

  private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    BASE64Encoder base64Encoder = new BASE64Encoder();
    return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));
  }
}
