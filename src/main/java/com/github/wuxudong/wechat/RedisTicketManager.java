package com.github.wuxudong.wechat;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weixin.popular.api.TicketAPI;
import weixin.popular.bean.ticket.Ticket;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TicketManager ticket(jsapi | wx_card) 自动刷新
 *
 * @author wuxudong
 */
public class RedisTicketManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisTicketManager.class);

    private static Map<String, String> ticketMap;

    private static Set<Pair<String, String>> appConfig = new HashSet<Pair<String, String>>();

    private static final String KEY_JOIN = "__";


    /**
     * 初始化ticket(jsapi) 刷新
     * 依赖TokenManager
     *
     * @param appid appid
     * @since 2.6.1
     */
    public static void init(final RedissonClient redissonClient, final String appid) {
        init(redissonClient, appid, "jsapi");
    }

    /**
     * 初始化ticket 刷新
     * 依赖TokenManager
     *
     * @param appid appid
     * @param types ticket 类型  [jsapi,wx_card]
     * @since 2.8.2
     */
    public static void init(final RedissonClient redissonClient, final String appid, String... types) {
        ticketMap = redissonClient.getMap("wechat_ticket");

        for (final String type : types) {
            appConfig.add(new ImmutablePair<String, String>(appid, type));
        }

        for (final String type : types) {
            doRun(appid, type);
        }
    }

    private static void doRun(final String appid, final String type) {
        try {
            String access_token = RedisTokenManager.getToken(appid);
            Ticket ticket = TicketAPI.ticketGetticket(access_token, type);

            final String key = appid + KEY_JOIN + type;
            ticketMap.put(key, ticket.getTicket());
            logger.info("TICKET refurbish with appid:{} type:{}", appid, type);
        } catch (Exception e) {
            logger.error("TICKET refurbish error with appid:{} type:{}", appid, type);
            e.printStackTrace();
        }
    }

    public static void refreshAll() {
        for (Pair<String, String> pair : appConfig) {
            doRun(pair.getLeft(), pair.getRight());
        }
    }

    /**
     * 获取 ticket(jsapi)
     *
     * @param appid appid
     * @return ticket
     */
    public static String getTicket(final String appid) {
        return getTicket(appid, "jsapi");
    }


    /**
     * 获取 ticket
     *
     * @param appid appid
     * @param type  jsapi or wx_card
     * @return ticket
     */
    public static String getTicket(final String appid, String type) {
        return ticketMap.get(appid + KEY_JOIN + type);
    }


    /**
     * 获取第一个appid 的第一个类型的 ticket
     * 适用于单一微信号
     *
     * @return ticket
     */
    public static String getDefaultTicket() {
        Object[] objs = ticketMap.values().toArray();
        return objs.length > 0 ? objs[0].toString() : null;
    }

}
