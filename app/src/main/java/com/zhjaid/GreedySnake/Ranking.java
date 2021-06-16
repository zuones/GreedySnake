package com.zhjaid.GreedySnake;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;

/**
 * from:zhjaid
 * email:zhjaid@163.com
 */
public class Ranking extends BmobObject {
    public String getUserNike() {
        return UserNike;
    }

    public void setUserNike(String userNike) {
        UserNike = userNike;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    private String UserNike;//昵称
    private int score;//最高分数
    private int frequency;//游戏次数
}
