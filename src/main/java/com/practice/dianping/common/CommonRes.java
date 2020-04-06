package com.practice.dianping.common;

public class CommonRes {
  private String status;

  private Object data;

  public static CommonRes create(Object result){
    return CommonRes.create(result, "success");
  }

  public static CommonRes create(Object result, String status){
    CommonRes commonRes = new CommonRes();
    commonRes.setStatus(status);
    commonRes.setData(result);
    return commonRes;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
