package com.jasper.fsm;

import lombok.extern.slf4j.Slf4j;

// 停止状态
@Slf4j
public class StoppedState implements State {
    @Override
    public void pressPlay(Player player) {
        log.info("【停止 -> 播放】开始播放音乐");
        player.setState(new PlayingState()); // 切换到播放状态
    }

    @Override
    public void pressPause(Player player) {
        log.info("当前已停止，无法暂停");
    }
}