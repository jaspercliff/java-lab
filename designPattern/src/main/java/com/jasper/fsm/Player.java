package com.jasper.fsm;

public class Player {
    private State currentState;

    public Player() {
        // 设置初始状态为“停止”
        this.currentState = new StoppedState();
    }

    // 允许状态对象修改播放器的状态
    public void setState(State state) {
        this.currentState = state;
    }

    // 用户点击播放按钮
    public void pressPlayButton() {
        currentState.pressPlay(this);
    }

    // 用户点击暂停按钮
    public void pressPauseButton() {
        currentState.pressPause(this);
    }
}