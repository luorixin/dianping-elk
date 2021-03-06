package com.practice.dianping.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.practice.dianping.common.BusinessException;
import com.practice.dianping.common.EmBusinessError;
import com.practice.dianping.dal.ShopModelMapper;
import com.practice.dianping.model.CategoryModel;
import com.practice.dianping.model.SellerModel;
import com.practice.dianping.model.ShopModel;
import com.practice.dianping.recommend.RecommendService;
import com.practice.dianping.recommend.RecommendSortService;
import com.practice.dianping.service.CategoryService;
import com.practice.dianping.service.SellerService;
import com.practice.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

  @Resource
  private ShopModelMapper shopModelMapper;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private SellerService sellerService;

  @Autowired
  private RestHighLevelClient highLevelClient;

  @Autowired
  private RecommendService recommendService;

  @Autowired
  private RecommendSortService recommendSortService;

  @Override
  @Transactional
  public ShopModel create(ShopModel shopModel) throws BusinessException {
    shopModel.setCreatedAt(new Date());
    shopModel.setUpdatedAt(new Date());

    SellerModel sellerModel = sellerService.get(shopModel.getSellerId());
    if (sellerModel == null) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户不存在");
    }
    if (sellerModel.getDisabledFlag().intValue() == 1) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户已禁用");
    }

    CategoryModel categoryModel = categoryService.get(shopModel.getCategoryId());
    if (categoryModel == null) {
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "类目不存在");
    }
    shopModelMapper.insertSelective(shopModel);
    return get(shopModel.getId());
  }

  @Override
  public ShopModel get(Integer id) {
    ShopModel shopModel = shopModelMapper.selectByPrimaryKey(id);
    if (shopModel == null) {
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

  @Override
  public List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
    List<Integer> shopIds = recommendService.recall(463);
    shopIds = recommendSortService.sort(shopIds, 463);
    List<ShopModel> shopModels = shopIds.stream().map(id -> {
      ShopModel shopModel = get(id);
      shopModel.setIconUrl("/static/image/shopcover/xchg.jpg");
      shopModel.setDistance(100);
      return shopModel;
    }).collect(Collectors.toList());

//    List<ShopModel> shopModels = shopModelMapper.recommend(longitude, latitude);
//    shopModels.forEach(shopModel -> {
//      shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
//      shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
//    });
    return shopModels;
  }

  @Override
  public List<ShopModel> search(BigDecimal longitude,
                                BigDecimal latitude, String keyword, Integer orderby,
                                Integer categoryId, String tags) {
    List<ShopModel> shopModelList = shopModelMapper.search(longitude, latitude, keyword, orderby, categoryId, tags);
    shopModelList.forEach(shopModel -> {
      shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
      shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
    });
    return shopModelList;
  }

  @Override
  public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
    return shopModelMapper.searchGroupByTags(keyword, categoryId, tags);
  }

  @Override
  public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) throws IOException {
    Map<String, Object> result = new HashMap<>();
//    SearchRequest searchRequest = new SearchRequest("shop");
//    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//    sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
//    sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//    searchRequest.source(sourceBuilder);
//    List<Integer> shopIdsList = new ArrayList<>();
//    SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//    SearchHit[] hits = searchResponse.getHits().getHits();
//    for (SearchHit hit: hits){
//      shopIdsList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//    }
//    List<ShopModel> shopModels = shopIdsList.stream().map(id -> {
//      return get(id);
//    }).collect(Collectors.toList());
    Request request = new Request("GET", "/shop/_search");
    JSONObject jsonRequestObj = new JSONObject();
    // 构建source
    jsonRequestObj.put("_source", "*");
    // 构建script_fields
    jsonRequestObj.put("script_fields", new JSONObject());
    jsonRequestObj.getJSONObject("script_fields").put("distance", new JSONObject());
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script", new JSONObject());
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
      .put("source", "haversin(lat, lon, doc['location'].lat, doc['location'].lon)");
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
      .put("lang", "expression");
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
      .put("params", new JSONObject());
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
      .getJSONObject("params").put("lat", latitude);
    jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
      .getJSONObject("params").put("lon", longitude);

    // 构建query
    Map<String, Object> cixingMap = analyzeCategoryKeyword(keyword);
    // 影响召回排序，一般2者不能同时用
    boolean isAffectFilter = false;
    boolean isAffectSort = true; // 优先使用，除非结果为空，如果结果为空就影响召回

    jsonRequestObj.put("query", new JSONObject());
    jsonRequestObj.getJSONObject("query").put("function_score", new JSONObject());
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("query", new JSONObject());
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .put("bool", new JSONObject());
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .getJSONObject("bool").put("must", new JSONArray());

    // 构建match query
    int queryIndex = 0;
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .getJSONObject("bool").getJSONArray("must").add(new JSONObject());

    if (cixingMap.keySet().size() > 0 && isAffectFilter) {
//      "bool": {
//        "must": [
//        {
//          "bool": {
//          "should": [
//          {"match": {"name": {"query": "凯悦","boost": 0.1}}},
//          {"term": {"category_id": {"value": "2", "boost": 0.1}}}
//                ]
//        }
//        },
//          ]
//      }
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("bool", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .put("should", new JSONArray());
      int filterQueryIndex = 0;
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .getJSONArray("should").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .getJSONArray("should").getJSONObject(filterQueryIndex)
        .put("match", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .getJSONArray("should").getJSONObject(filterQueryIndex)
        .getJSONObject("match").put("name", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .getJSONArray("should").getJSONObject(filterQueryIndex)
        .getJSONObject("match").getJSONObject("name").put("query", keyword);
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
        .getJSONArray("should").getJSONObject(filterQueryIndex)
        .getJSONObject("match").getJSONObject("name").put("boost", 0.1);

      // 遍历词性库 影响召回
      // 增加 {"term": {"category_id": {"value": "2", "boost": 0}}}
      for (String key : cixingMap.keySet()) {
        filterQueryIndex++;
        Integer cixingCategoryId = (Integer) cixingMap.get(key);
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
          .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
          .getJSONArray("should").add(new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
          .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
          .getJSONArray("should").getJSONObject(filterQueryIndex).put("term", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
          .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
          .getJSONArray("should").getJSONObject(filterQueryIndex).getJSONObject("term").put("category_id", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
          .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
          .getJSONArray("should").getJSONObject(filterQueryIndex).getJSONObject("term")
          .getJSONObject("category_id").put("value", cixingCategoryId);
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
          .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool")
          .getJSONArray("should").getJSONObject(filterQueryIndex).getJSONObject("term")
          .getJSONObject("category_id").put("boost", 0);
      }
    } else {
//      "bool": {
//        "must": [
//        {"match": {"name": {"query": "凯悦","boost": 0.1}}},
//          ]
//      }
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("match", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
        .getJSONObject("match").put("name", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
        .getJSONObject("match").getJSONObject("name").put("query", keyword);
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
        .getJSONObject("match").getJSONObject("name").put("boost", 0.1);
    }

//        {"term": {"seller_disabled_flag": 0}},

    queryIndex++;
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
      .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
      .getJSONObject("term").put("seller_disabled_flag", 0);

//        {"term": {"tags": "落地大窗"}}
    if (tags != null) {
      queryIndex++;
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
        .getJSONObject("term").put("tags", tags);
    }

    if (categoryId != null) {
      queryIndex++;
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
        .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex)
        .getJSONObject("term").put("category_id", categoryId);
    }

    // functions
    jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("functions", new JSONArray());
    int functionIndex = 0;
    if (orderby == null) {
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("gauss", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("gauss")
        .getJSONObject("location").put("origin", latitude.toString() + "," + longitude.toString());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("gauss")
        .getJSONObject("location").put("scale", "100km");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("gauss")
        .getJSONObject("location").put("offset", "0km");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("gauss")
        .getJSONObject("location").put("decay", "0.5");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("weight", 9);

      functionIndex++;
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "remark_score");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("weight", 0.2);

      functionIndex++;
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "seller_remark_score");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("weight", 0.1);

      // 词性排序
//      {
//        "filter": {"term" : {"category_id": 2}},
//        "weight": 0.2
//      }
      if (cixingMap.keySet().size() > 0 && isAffectSort) {
        for (String key : cixingMap.keySet()) {
          functionIndex++;
          jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
          jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
            .getJSONObject(functionIndex).put("filter", new JSONObject());
          jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
            .getJSONObject(functionIndex).getJSONObject("filter").put("term", new JSONObject());
          jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
            .getJSONObject(functionIndex).getJSONObject("filter").getJSONObject("term").put("category_id", cixingMap.get(key));
          jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
            .getJSONObject(functionIndex).put("weight", 0.2);
        }
      }

      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "sum");

    } else {
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "price_per_man");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
        .getJSONObject(functionIndex).put("weight", 1);
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
      jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "replace");
    }

    // sort
    jsonRequestObj.put("sort", new JSONArray());
    jsonRequestObj.getJSONArray("sort").add(new JSONObject());
    jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score", new JSONObject());
    if (orderby == null) {
      jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "desc");
    } else {
      jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order", "asc");
    }

    // aggs
    jsonRequestObj.put("aggs", new JSONObject());
    jsonRequestObj.getJSONObject("aggs").put("group_by_tags", new JSONObject());
    jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms", new JSONObject());
    jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field", "tags");

    String reqJson = jsonRequestObj.toJSONString();


    request.setJsonEntity(reqJson);
    Response response = highLevelClient.getLowLevelClient().performRequest(request);
    String responseStr = EntityUtils.toString(response.getEntity());
    JSONObject jsonObject = JSONObject.parseObject(responseStr);

    List<ShopModel> shopModels = new ArrayList<>();
    JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject jsonObject1 = jsonArray.getJSONObject(i);
      Integer id = new Integer(jsonObject1.get("_id").toString());
      BigDecimal distance = new BigDecimal(jsonObject1.getJSONObject("fields").getJSONArray("distance").get(0).toString());
      ShopModel shopModel = get(id);
      shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
      shopModels.add(shopModel);
    }

    List<Map> tagsList = new ArrayList<>();
    JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
    for (int i = 0; i < tagsJsonArray.size(); i++) {
      JSONObject jsonObject1 = tagsJsonArray.getJSONObject(i);
      Map<String, Object> tagMap = new HashMap<>();
      tagMap.put("tags", jsonObject1.getString("key"));
      tagMap.put("num", jsonObject1.getInteger("doc_count"));
      tagsList.add(tagMap);
    }

    result.put("tags", tagsList);
    result.put("shop", shopModels);
    return result;
  }

  // 构造分词函数识别器
  private Map<String, Object> analyzeCategoryKeyword(String keyword) throws IOException {
    Map<String, Object> res = new HashMap<>();
    Request request = new Request("GET", "/shop/_analyze");
    request.setJsonEntity("{ \"field\": \"name\",\"text\": \"" + keyword + "\"}");
    Response response = highLevelClient.getLowLevelClient().performRequest(request);
    String responseStr = EntityUtils.toString(response.getEntity());
    JSONObject jsonObject = JSONObject.parseObject(responseStr);
    JSONArray jsonArray = jsonObject.getJSONArray("tokens");
    for (int i = 0; i < jsonArray.size(); i++) {
      String token = jsonArray.getJSONObject(i).getString("token");
      Integer categoryId = getCategoryIdByToken(token);
      if (categoryId != null) {
        res.put(token, categoryId);
      }
    }
    return res;
  }

  private Integer getCategoryIdByToken(String token) {
    for (Integer key : categoryWorkMap.keySet()) {
      List<String> tokenList = categoryWorkMap.get(key);
      if (tokenList.contains(token)) {
        return key;
      }
    }
    return null;
  }

  private Map<Integer, List<String>> categoryWorkMap = new HashMap<>();

  @PostConstruct
  public void init() {
    categoryWorkMap.put(1, new ArrayList<>());
    categoryWorkMap.put(2, new ArrayList<>());

    categoryWorkMap.get(1).add("吃饭");
    categoryWorkMap.get(1).add("下午茶");

    categoryWorkMap.get(2).add("休息");
    categoryWorkMap.get(2).add("睡觉");
    categoryWorkMap.get(2).add("住宿");
  }
}
