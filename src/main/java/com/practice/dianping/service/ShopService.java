package com.practice.dianping.service;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.model.ShopModel;

import java.util.List;

public interface ShopService {
  ShopModel create(ShopModel shopModel) throws BusinessException;

  ShopModel get(Integer id);

  List<ShopModel> selectAll();

  Integer countAllShop();
}
