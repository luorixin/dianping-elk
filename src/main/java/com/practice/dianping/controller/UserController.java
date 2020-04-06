package com.practice.dianping.controller;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.CommonError;
import com.practice.dianping.common.CommonRes;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.model.UserModel;
import com.practice.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("/user")
@RequestMapping("/user")
public class UserController {

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
