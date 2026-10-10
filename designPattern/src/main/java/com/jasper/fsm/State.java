package com.jasper.fsm;

public interface State {
    void pressPlay(Player player);
    void pressPause(Player player);
}