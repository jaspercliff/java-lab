package jasper.juc.tools.phaser;

import java.util.concurrent.Phaser;

public class PhaserDemo {

    public static void main(String[] args) {

        Phaser phaser = new Phaser(3);

        // 观察者线程
        new Thread(
                        () -> {
                            int phase = phaser.getPhase();

                            while (!phaser.isTerminated()) {

                                phaser.awaitAdvance(phase);

                                phase = phaser.getPhase();

                                System.out.println(
                                        "\n========== 进入 Phase "
                                                + phase
                                                + " 当前人数:"
                                                + phaser.getRegisteredParties()
                                                + " ==========\n");
                            }
                        })
                .start();

        new Thread(new Player("A", phaser)).start();
        new Thread(new Player("B", phaser)).start();
        new Thread(new Player("C", phaser)).start();
    }
}

class Player implements Runnable {

    private final String name;
    private final Phaser phaser;

    public Player(String name, Phaser phaser) {
        this.name = name;
        this.phaser = phaser;
    }

    @Override
    public void run() {

        try {

            // ================= phase 0 =================

            System.out.println(name + " 加载地图...");
            Thread.sleep(random());

            System.out.println(name + " 地图加载完成");

            phaser.arriveAndAwaitAdvance();

            // ================= phase 1 =================

            System.out.println(name + " 选择英雄...");
            Thread.sleep(random());

            // A 玩家动态拉人
            if ("A".equals(name)) {

                System.out.println("新玩家 D 加入游戏");

                // 动态注册
                phaser.register();

                new Thread(new Player("D", phaser)).start();
            }

            // B 玩家退出
            if ("B".equals(name)) {

                System.out.println(name + " 退出游戏");

                phaser.arriveAndDeregister();

                return;
            }

            System.out.println(name + " 英雄选择完成");

            phaser.arriveAndAwaitAdvance();

            // ================= phase 2 =================

            System.out.println(name + " 开始战斗...");
            Thread.sleep(random());

            // arrive 示例
            System.out.println(name + " 战斗完成，通知系统");

            phaser.arrive();

            System.out.println(name + " 去领取奖励了（不等待别人）");

            // 最终退出
            phaser.arriveAndDeregister();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long random() {
        return (long) (Math.random() * 2000);
    }
}
