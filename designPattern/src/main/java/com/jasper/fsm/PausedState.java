package com.jasper.fsm;

import lombok.extern.slf4j.Slf4j;

// 暂停状态
@Slf4j
public class PausedState implements State {
    @Override
    public void pressPlay(Player player) {
        log.info("【暂停 -> 播放】恢复播放");
        player.setState(new PlayingState()); // 切回播放状态
    }

    @Override
    public void pressPause(Player player) {
        log.info("已经在暂停状态了");
    }
}