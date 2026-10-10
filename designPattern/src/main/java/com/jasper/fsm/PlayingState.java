package com.jasper.fsm;

import lombok.extern.slf4j.Slf4j;

// 播放状态
@Slf4j
public class PlayingState implements State {
    @Override
    public void pressPlay(Player player) {
        log.info("已经在播放了");
    }

    @Override
    public void pressPause(Player player) {
        log.info("【播放 -> 暂停】音乐已暂停");
        player.setState(new PausedState()); // 切换到暂停状态
    }
}