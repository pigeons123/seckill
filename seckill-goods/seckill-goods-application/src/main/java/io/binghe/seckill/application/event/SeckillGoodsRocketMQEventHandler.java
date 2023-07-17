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
package io.binghe.seckill.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.binghe.seckill.application.cache.service.SeckillGoodsCacheService;
import io.binghe.seckill.application.cache.service.SeckillGoodsListCacheService;
import io.binghe.seckill.common.constants.SeckillConstants;
import io.binghe.seckill.goods.domain.event.SeckillGoodsEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 基于RocketMQ的商品事件处理器
 * @github https://github.com/binghe001
 * @copyright 公众号: 冰河技术
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS)
public class SeckillGoodsRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsRocketMQEventHandler.class);
    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|goodsEvent|接收秒杀品事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|goodsEvent|接收秒杀品事件参数错误" );
            return;
        }
        SeckillGoodsEvent seckillGoodsEvent = this.getEventMessage(message);
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
    }

    private SeckillGoodsEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillGoodsEvent.class);
    }
}
