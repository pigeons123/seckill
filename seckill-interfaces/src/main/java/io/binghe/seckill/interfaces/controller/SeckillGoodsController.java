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
package io.binghe.seckill.interfaces.controller;

import io.binghe.seckill.application.command.SeckillGoodsCommond;
import io.binghe.seckill.application.service.SeckillGoodsService;
import io.binghe.seckill.domain.code.HttpCode;
import io.binghe.seckill.domain.model.dto.SeckillGoodsDTO;
import io.binghe.seckill.domain.model.entity.SeckillGoods;
import io.binghe.seckill.domain.response.ResponseMessage;
import io.binghe.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 商品接口
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@RestController
@RequestMapping(value = "/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 保存秒杀商品
     */
    @RequestMapping(value = "/saveSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillGoods(SeckillGoodsCommond seckillGoodsCommond){
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommond);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(Long id){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsId(id));
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsByActivityId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(Long activityId){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }

    /**
     * 更新商品状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(Integer status, Long id){
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

    /**
     * 获取商品列表(带缓存)
     */
    @RequestMapping(value = "/getSeckillGoodsList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsByActivityId(Long activityId, Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 获取商品详情（带缓存）
     */
    @RequestMapping(value = "/getSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(Long id, Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }
}
