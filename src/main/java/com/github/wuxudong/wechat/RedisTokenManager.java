package com.github.wuxudong.wechat;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.token.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * TokenManager token 自动刷新
 *
 * @author wuxudong
 */
public class RedisTokenManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisTokenManager.class);

    private static Map<String, String> tokenMap;

    private static Map<String, String> appConfig = new HashMap<String, String>();

    /**
     * 初始化token 刷新，每118分钟刷新一次。
     *
     * @param appid  appid
     * @param secret secret
     */
    public static void init(RedissonClient redissonClient, final String appid, final String secret) {

        appConfig.put(appid, secret);
        tokenMap = redissonClient.getMap("wechat_access_token");

        doRun(appid, secret);

        logger.info("appid:{}", appid);
    }


    private static void doRun(final String appid, final String secret) {
        try {
            Token token = TokenAPI.token(appid, secret);
            tokenMap.put(appid, token.getAccess_token());
            logger.info("ACCESS_TOKEN refurbish with appid:{}", appid);
        } catch (Exception e) {
            logger.error("ACCESS_TOKEN refurbish error with appid:{}", appid);
            e.printStackTrace();
        }
    }

    public static void refreshAll() {
        for (Map.Entry<String, String> entry : appConfig.entrySet()) {
            doRun(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取 access_token
     *
     * @param appid appid
     * @return token
     */
    public static String getToken(String appid) {
        return tokenMap.get(appid);
    }

    /**
     * 获取第一个appid 的 access_token
     * 适用于单一微信号
     *
     * @return token
     */
    public static String getDefaultToken() {
        Object[] objs = tokenMap.values().toArray();
        return objs.length > 0 ? objs[0].toString() : null;
    }

}
