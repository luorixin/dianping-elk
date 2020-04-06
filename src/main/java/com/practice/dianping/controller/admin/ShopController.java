package com.practice.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.practice.dianping.common.AdminPermission;
import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.CommonUtil;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.model.ShopModel;
import com.practice.dianping.request.ShopCreateReq;
import com.practice.dianping.request.PageQuery;
import com.practice.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller("/admin/shop")
@RequestMapping("/admin/shop")
public class ShopController {
  @Autowired
  private ShopService shopService;

  @RequestMapping("/index")
  @AdminPermission
  public ModelAndView index(PageQuery pageQuery) {
    PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
    List<ShopModel> shopModels = shopService.selectAll();
    PageInfo<ShopModel> shopModelPageInfo = new PageInfo<>(shopModels);

    ModelAndView modelAndView = new ModelAndView("/admin/shop/index.html");
    modelAndView.addObject("data", shopModelPageInfo);
    modelAndView.addObject("CONTROLLER_NAME", "shop");
    modelAndView.addObject("ACTION_NAME", "index");
    return modelAndView;
  }

  @RequestMapping("/createpage")
  @AdminPermission
  public ModelAndView createpage() {
    ModelAndView modelAndView = new ModelAndView("/admin/shop/create.html");
    modelAndView.addObject("CONTROLLER_NAME", "shop");
    modelAndView.addObject("ACTION_NAME", "index");
    return modelAndView;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @AdminPermission
  public String create(@Valid ShopCreateReq shopCreateReq, BindingResult bindingResult) throws BusinessException {
    if (bindingResult.hasErrors()) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
    }
    ShopModel shopModel = new ShopModel();
    shopModel.setIconUrl(shopCreateReq.getIconUrl());
    shopModel.setAddress(shopCreateReq.getAddress());
    shopModel.setCategoryId(shopCreateReq.getCategoryId());
    shopModel.setEndTime(shopCreateReq.getEndTime());
    shopModel.setStartTime(shopCreateReq.getStartTime());
    shopModel.setLongitude(shopCreateReq.getLongitude());
    shopModel.setLatitude(shopCreateReq.getLatitude());
    shopModel.setName(shopCreateReq.getName());
    shopModel.setPricePerMan(shopCreateReq.getPricePerMan());
    shopModel.setSellerId(shopCreateReq.getSellerId());

    shopService.create(shopModel);
    return "redirect:/admin/shop/index";
  }
}
