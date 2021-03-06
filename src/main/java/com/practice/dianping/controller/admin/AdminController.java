package com.practice.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.practice.dianping.common.AdminPermission;
import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.CommonRes;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.request.PageQuery;
import com.practice.dianping.service.CategoryService;
import com.practice.dianping.service.SellerService;
import com.practice.dianping.service.ShopService;
import com.practice.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {
  @Value("${admin.email}")
  private String emial;

  @Value("${admin.encryptPassword}")
  private String encryptPassword;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Autowired
  private UserService userService;

  @Autowired
  private SellerService sellerService;

  @Autowired
  private ShopService shopService;

  @Autowired
  private CategoryService categoryService;

  public static final String CURRENT_ADMIN_SESSION = "currentAdminSession";

  @RequestMapping("/index")
  @AdminPermission
  public ModelAndView index() {
    ModelAndView modelAndView = new ModelAndView("/admin/admin/index");
    modelAndView.addObject("userCount", userService.countAllUser());
    modelAndView.addObject("shopCount", shopService.countAllShop());
    modelAndView.addObject("categoryCount", categoryService.countAllCategory());
    modelAndView.addObject("sellerCount", sellerService.countAllSeller());
    modelAndView.addObject("CONTROLLER_NAME", "admin");
    modelAndView.addObject("ACTION_NAME", "index");
    return modelAndView;
  }

  @RequestMapping("/indexjson")
  @AdminPermission(produceType = "application/json")
  @ResponseBody
  public CommonRes indexjson() {
    return CommonRes.create(null);
  }

  @RequestMapping("/loginpage")
  public ModelAndView loginpage() {
    ModelAndView modelAndView = new ModelAndView("/admin/admin/login");
    return modelAndView;
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(@RequestParam("email") String email, @RequestParam("password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
    }
    if (this.encryptPassword.equals(this.encodeByMd5(password)) && this.emial.equals(email)) {
      httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION, email);
      return "redirect:/admin/admin/index";
    } else {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户名密码错误");
    }
  }

  private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    BASE64Encoder base64Encoder = new BASE64Encoder();
    return base64Encoder.encode(messageDigest.digest(str.getBytes("utf-8")));
  }
}
