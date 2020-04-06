package com.practice.dianping.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.practice.dianping.common.*;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.request.PageQuery;
import com.practice.dianping.request.CategoryCreateReq;
import com.practice.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller("/admin/category")
@RequestMapping("/admin/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  @RequestMapping("/index")
  @AdminPermission
  public ModelAndView index(PageQuery pageQuery) {
    PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
    List<CategoryModel> categoryModels = categoryService.selelctAll();
    PageInfo<CategoryModel> categoryModelPageInfo = new PageInfo<>(categoryModels);

    ModelAndView modelAndView = new ModelAndView("/admin/category/index.html");
    modelAndView.addObject("data", categoryModelPageInfo);
    modelAndView.addObject("CONTROLLER_NAME", "category");
    modelAndView.addObject("ACTION_NAME", "index");
    return modelAndView;
  }

  @RequestMapping("/createpage")
  @AdminPermission
  public ModelAndView createpage() {
    ModelAndView modelAndView = new ModelAndView("/admin/category/create.html");
    modelAndView.addObject("CONTROLLER_NAME", "category");
    modelAndView.addObject("ACTION_NAME", "index");
    return modelAndView;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @AdminPermission
  public String create(@Valid CategoryCreateReq categoryCreateReq, BindingResult bindingResult) throws BusinessException {
    if (bindingResult.hasErrors()) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processErrorString(bindingResult));
    }
    CategoryModel categoryModel = new CategoryModel();
    categoryModel.setName(categoryCreateReq.getName());
    categoryModel.setIconUrl(categoryCreateReq.getIconUrl());
    categoryModel.setSort(categoryCreateReq.getSort());
    categoryService.create(categoryModel);
    return "redirect:/admin/category/index";
  }


}
