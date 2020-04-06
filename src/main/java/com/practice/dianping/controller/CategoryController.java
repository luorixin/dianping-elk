package com.practice.dianping.controller;

import com.practice.dianping.common.CommonRes;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("/category")
@RequestMapping("/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  @ResponseBody
  @RequestMapping("/list")
  public CommonRes list(){
    List<CategoryModel> categoryModels = categoryService.selelctAll();
    return CommonRes.create(categoryModels);
  }
}
