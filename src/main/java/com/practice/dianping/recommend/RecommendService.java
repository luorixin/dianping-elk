package com.practice.dianping.recommend;

import com.practice.dianping.dal.RecommendDOMapper;
import com.practice.dianping.model.RecommendDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendService{

  @Autowired
  private RecommendDOMapper recommendDOMapper;

  // 召回数据，根据userid召回shopidlist
  public List<Integer> recall(Integer userId){
    RecommendDO recommendDO = recommendDOMapper.selectByPrimaryKey(userId);
    if (recommendDO == null){
      recommendDO = recommendDOMapper.selectByPrimaryKey(9999999);
    }
    String[] shopIdArr = recommendDO.getRecommend().split(",");
    List<Integer> shopIdList = new ArrayList<>();
    for (int i=0; i<shopIdArr.length; i++){
      shopIdList.add(Integer.valueOf(shopIdArr[i]));
    }
    return shopIdList;
  }
}
