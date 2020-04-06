package com.practice.dianping.service;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.model.CategoryModel;

import java.util.List;

public interface CategoryService {
  CategoryModel create(CategoryModel categoryModel) throws BusinessException;

  CategoryModel get(Integer id);

  List<CategoryModel> selelctAll();

  Integer countAllCategory();
}
