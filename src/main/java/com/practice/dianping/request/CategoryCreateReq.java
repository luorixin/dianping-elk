package com.practice.dianping.request;

import javax.validation.constraints.NotBlank;

public class CategoryCreateReq {
  @NotBlank(message = "名字不能为空")
  private String name;
  @NotBlank(message = "url不能为空")
  private String iconUrl;
  @NotBlank(message = "sort不能为空")
  private Integer sort;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }
}
