package com.practice.dianping.controller;

import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.CommonRes;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.model.ShopModel;
import com.practice.dianping.service.CategoryService;
import com.practice.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/shop")
@RequestMapping("/shop")
public class ShopController {
  @Autowired
  private ShopService shopService;

  @Autowired
  private CategoryService categoryService;

  @RequestMapping("/recommend")
  @ResponseBody
  public CommonRes recommend(@RequestParam(name = "longitude") BigDecimal longitude,
                             @RequestParam(name = "latitude") BigDecimal latitude) throws BusinessException {
    if (longitude == null || latitude == null) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
    }
    List<ShopModel> shopModels = shopService.recommend(longitude, latitude);
    return CommonRes.create(shopModels);
  }

  @RequestMapping("/search")
  @ResponseBody
  public CommonRes search(@RequestParam(name="longitude")BigDecimal longitude,
                          @RequestParam(name="latitude")BigDecimal latitude,
                          @RequestParam(name="keyword")String keyword,
                          @RequestParam(name="orderby",required = false)Integer orderby,
                          @RequestParam(name="categoryId",required = false)Integer categoryId,
                          @RequestParam(name="tags",required = false)String tags) throws BusinessException, IOException {
    if(StringUtils.isEmpty(keyword) || longitude == null || latitude == null){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
    }

//    List<ShopModel> shopModelList = shopService.search(longitude,latitude,keyword,orderby,categoryId,tags);
    List<ShopModel> shopModelList = (List<ShopModel>) shopService.searchES(longitude,latitude,keyword,orderby,categoryId,tags).get("shop");
    List<CategoryModel> categoryModelList = categoryService.selelctAll();
    List<Map<String,Object>> tagsAggregation = shopService.searchGroupByTags(keyword,categoryId,tags);
    Map<String,Object> resMap = new HashMap<>();
    resMap.put("shop",shopModelList);
    resMap.put("category",categoryModelList);
    resMap.put("tags",tagsAggregation);
    return CommonRes.create(resMap);

  }
}
