package com.practice.dianping.service.impl;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.dal.CategoryModelMapper;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Resource
  private CategoryModelMapper categoryModelMapper;

  @Override
  @Transactional
  public CategoryModel create(CategoryModel categoryModel) throws BusinessException {
    categoryModel.setCreatedAt(new Date());
    categoryModel.setUpdatedAt(new Date());
    try{
      categoryModelMapper.insertSelective(categoryModel);
    }catch (DuplicateKeyException ex){
      throw new BusinessException(EmBusinessError.CATEGORY_NAME_DUPLICATED);
    }
    return get(categoryModel.getId());
  }

  @Override
  public CategoryModel get(Integer id) {
    return categoryModelMapper.selectByPrimaryKey(id);
  }

  @Override
  public List<CategoryModel> selelctAll() {
    return categoryModelMapper.selectAll();
  }

  @Override
  public Integer countAllCategory() {
    return categoryModelMapper.countAllCategory();
  }
}
