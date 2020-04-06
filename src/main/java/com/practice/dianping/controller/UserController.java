package com.practice.dianping.controller;

import com.practice.dianping.common.*;
import com.practice.dianping.model.UserModel;
import com.practice.dianping.request.LoginReq;
import com.practice.dianping.request.RegisterReq;
import com.practice.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Controller("/user")
@RequestMapping("/user")
public class UserController {

  public static final String CURRENT_USER_SESSION = "currentUserSession";

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Autowired
  private UserService userService;

  @RequestMapping("/test")
  @ResponseBody
  public String test() {
    return "test";
  }

  @RequestMapping("/index")
  public ModelAndView index(){
    String userName = "test";
    ModelAndView modelAndView = new ModelAndView("index.html");
    modelAndView.addObject("name", userName);
    return modelAndView;
  }

  @RequestMapping("/register")
  @ResponseBody
  public CommonRes register(@Valid @RequestBody RegisterReq registerReq, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    if (bindingResult.hasErrors()){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
    }
    UserModel registerUser = new UserModel();
    registerUser.setTelphone(registerReq.getTelphone());
    registerUser.setPassword(registerReq.getPassword());
    registerUser.setNickName(registerReq.getNickName());
    registerUser.setGender(registerReq.getGender());
    UserModel userModel = userService.register(registerUser);
    return CommonRes.create(userModel);
  }

  @RequestMapping("/login")
  @ResponseBody
  public CommonRes login(@Valid @RequestBody LoginReq loginReq, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    if (bindingResult.hasErrors()){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
    }
    UserModel userModel = userService.login(loginReq.getTelphone(), loginReq.getPassword());
    httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION, userModel);
    return CommonRes.create(userModel);
  }

  @RequestMapping("/logout")
  @ResponseBody
  public CommonRes logout() {

    httpServletRequest.getSession().invalidate();
    return CommonRes.create(null);
  }

  @RequestMapping("/getcurrentuser")
  @ResponseBody
  public CommonRes getCurrentUser(){
    UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);
    return CommonRes.create(userModel);
  }

  @RequestMapping("/get")
  @ResponseBody
  public CommonRes get(@RequestParam(name = "id") Integer id) throws BusinessException {
    UserModel userModel = userService.getUser(id);
    if (userModel == null) {
//      return CommonRes.create(new CommonError(EmBusinessError.NO_OBJECT_FOUND));
      throw new BusinessException(EmBusinessError.NO_OBJECT_FOUND);
    }
    return CommonRes.create(userModel);
  }
}
