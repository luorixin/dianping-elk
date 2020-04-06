package com.practice.dianping.common;

public class CommonError {
  private Integer errCode;

  private String errMsg;

  public CommonError(Integer errCode, String errMsg) {
    this.errCode = errCode;
    this.errMsg = errMsg;
  }

  public CommonError(EmBusinessError emBusinessError) {
    this.errCode = emBusinessError.getErrCode();
    this.errMsg = emBusinessError.getErrMsg();
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
