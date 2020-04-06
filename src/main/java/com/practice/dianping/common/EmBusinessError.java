package com.practice.dianping.common;

public enum EmBusinessError {

  NO_OBJECT_FOUND(10001,"请求对象不存在"),
  NO_HANDLER_FOUND(10003,"请求地址不存在"),
  BIND_EXCEPTION_ERROR(10004,"请求参数错误"),
  PARAMETER_VALIDATION_ERROR(10005,"请求参数校验失败"),
  REGISTER_DUP_FAIL(20001,"用户已存在"),
  LOGIN_FAIL(20002,"手机号或者密码错误"),
  ADMIN_SHOULD_LOGIN(30001,"管理员需要先登录"),
  CATEGORY_NAME_DUPLICATED(40001,"品类名已存在"),
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
