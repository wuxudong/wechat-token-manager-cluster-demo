package com.github.wuxudong.wechat;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WechatRefresher {
    private static final int FIFTY_NINE_MIN = 59 * 60 * 1000;

    @Scheduled(cron = "0 0 */1 * * *")
    @SchedulerLock(name = "refreshWechat", lockAtMostFor = FIFTY_NINE_MIN, lockAtLeastFor = FIFTY_NINE_MIN)
    public void refreshWechat() {
        RedisTokenManager.refreshAll();
        RedisTicketManager.refreshAll();
    }

}
