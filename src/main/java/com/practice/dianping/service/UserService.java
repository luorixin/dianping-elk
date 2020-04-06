package com.practice.dianping.service;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.model.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface UserService {
  UserModel getUser(Integer id);

  UserModel register(UserModel registerUser) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;

  UserModel login(String telphone, String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException;

  Integer countAllUser();
}
