package com.practice.dianping.common;

public enum EmBusinessError {

  NO_OBJECT_FOUND(10001,"请求对象不存在"),
  NO_HANDLER_FOUND(10003,"请求地址不存在"),
  BIND_EXCEPTION_ERROR(10004,"请求参数错误"),
  UNKNOWN_ERROR(10002,"未知错误");

  private Integer errCode;

  private String errMsg;

  EmBusinessError(Integer errCode, String errMsg) {
    this.errCode = errCode;
    this.errMsg = errMsg;
  }

  public Integer getErrCode() {
    return errCode;
  }

  public void setErrCode(Integer errCode) {
    this.errCode = errCode;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
}
