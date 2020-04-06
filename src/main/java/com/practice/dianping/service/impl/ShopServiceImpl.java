package com.practice.dianping.service.impl;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.dal.ShopModelMapper;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.model.SellerModel;
import com.practice.dianping.model.ShopModel;
import com.practice.dianping.service.CategoryService;
import com.practice.dianping.service.SellerService;
import com.practice.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

  @Autowired
  private ShopModelMapper shopModelMapper;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private SellerService sellerService;

  @Override
  @Transactional
  public ShopModel create(ShopModel shopModel) throws BusinessException {
    shopModel.setCreatedAt(new Date());
    shopModel.setUpdatedAt(new Date());

    SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
    if (sellerModel == null){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户不存在");
    }
    if (sellerModel.getDisabledFlag().intValue() == 1){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户已禁用");
    }

    CategoryModel categoryModel = categoryService.get(shopModel.getCategoryId());
    if (categoryModel == null){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "类目不存在");
    }
    shopModelMapper.insertSelective(shopModel);
    return get(shopModel.getId());
  }

  @Override
  public ShopModel get(Integer id) {
    ShopModel shopModel = shopModelMapper.selectByPrimaryKey(id);
    if (shopModel == null){
      return null;
    }
    shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
    shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
    return shopModel;
  }

  @Override
  public List<ShopModel> selectAll() {
    List<ShopModel> shopModels = shopModelMapper.selectAll();
    shopModels.forEach(shopModel -> {
      shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
      shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
    });
    return shopModels;
  }

  @Override
  public Integer countAllShop() {
    return shopModelMapper.countAllShop();
  }
}
