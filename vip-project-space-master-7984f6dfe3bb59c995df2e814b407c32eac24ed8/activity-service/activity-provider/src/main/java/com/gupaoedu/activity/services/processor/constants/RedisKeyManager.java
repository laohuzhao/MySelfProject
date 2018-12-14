package com.gupaoedu.activity.services.processor.constants;


import com.gupaoedu.activity.dal.entitys.ActDrawAward;
import com.gupaoedu.activity.draw.bean.ActivityTurntableDrawReq;

/**


 */
public class RedisKeyManager {

    /**
     * 正在抽奖的key
     * @param activityTurntableDrawReq
     * @return
     */
    public static String getDrawingRedisKey(ActivityTurntableDrawReq activityTurntableDrawReq) {
        return DrawContants.DRAWING_PREFIX+String.valueOf(activityTurntableDrawReq.getUid());
    }

    public static String getAwardRedisKey(ActDrawAward actDrawAward){
        return DrawContants.DRAW_AWARD+actDrawAward.getAwardType()+":"+actDrawAward.getId();
    }
}
