/**
 * Copyright 2022-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.binghe.seckill.common.exception;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
public enum ErrorCode {

    SUCCESS(1001, "成功"),
    FAILURE(2001, "失败"),
    PARAMS_INVALID(2002, "参数错误"),
    USERNAME_IS_NULL(2003, "用户名不能为空"),
    PASSWORD_IS_NULL(2004, "密码不能为空"),
    USERNAME_IS_ERROR(2005, "用户名错误"),
    PASSWORD_IS_ERROR(2006, "密码错误"),
    SERVER_EXCEPTION(2007, "服务器异常"),
    STOCK_LT_ZERO(2008, "库存不足"),
    GOODS_NOT_EXISTS(2009, "当前商品不存在"),
    ACTIVITY_NOT_EXISTS(2010, "当前活动不存在"),
    BEYOND_LIMIT_NUM(2011, "下单数量不能超过限购数量"),
    USER_NOT_LOGIN(2012, "用户未登录"),
    TOKEN_EXPIRE(2013, "Token失效"),
    GOODS_OFFLINE(2014, "商品已下线"),
    DATA_PARSE_FAILED(2015, "数据解析失败"),
    RETRY_LATER(2016, "稍后再试"),
    USER_INVALID(2017, "当前账户异常，不能参与秒杀"),
    GOODS_PUBLISH(2018, "商品未上线"),
    ORDER_FAILED(2019, "下单失败"),
    BEYOND_TIME(2020, "超出活动时间"),
    GOODS_FINISH(2021, "商品已售罄"),
    REDUNDANT_SUBMIT(2022, "请勿重复下单"),
    ORDER_TOKENS_NOT_AVAILABLE(2023, "暂无可用库存"),
    ORDER_TASK_ID_INVALID(2024, "下单任务编号错误"),
    BUCKET_INIT_STOCK_ERROR(2025, "分桶总库存错误"),
    BUCKET_AVAILABLE_STOCK_ERROR(2026, "分桶可用库存错误"),
    BUCKET_INIT_AVAILABLE_STOCK_NOT_EQUALS(2027, "初始化分桶时，库存总数与可用库存数不匹配"),
    BUCKET_GOODSID_ERROR(2028, "秒杀商品id错误"),
    BUCKET_CREATE_FAILED(2029, "库存分桶失败"),
    BUCKET_CLOSED_FAILED(2030, "关闭分桶失败"),
    BUCKET_SOLD_BEYOND_TOTAL(2031, "已售商品数量大于要设置的总库存"),
    FREQUENTLY_ERROR(2032, "操作频繁，稍后再试"),
    STOCK_IS_NULL(2033, "商品库存不存在");

    private final Integer code;
    private final String mesaage;

    ErrorCode(Integer code, String mesaage) {
        this.code = code;
        this.mesaage = mesaage;
    }

    public Integer getCode() {
        return code;
    }

    public String getMesaage() {
        return mesaage;
    }

}
