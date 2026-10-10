package com.jasper.fsm;

public class Main {
    public static void main(String[] args) {
        Player player = new Player();

        player.pressPlayButton();  // 输出：【停止 -> 播放】开始播放音乐
        player.pressPlayButton();  // 输出：已经在播放了
        player.pressPauseButton(); // 输出：【播放 -> 暂停】音乐已暂停
        player.pressPlayButton();  // 输出：【暂停 -> 播放】恢复播放
    }
}