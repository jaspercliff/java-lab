package com.jasper.creational.builder;

import lombok.Data;

@Data
public class Computer {
    private String cpu;
    private String memory;
    private String disk;
    private String motherboard;
    // 构造函数参数较多
    public Computer(final Builder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.disk = builder.disk;
        this.motherboard = builder.motherboard;
    }

    public static class Builder{
        private String cpu;
        private String memory;
        private String disk;
        private String motherboard;
        public Builder cpu(String cpu){
            this.cpu = cpu;
            return this;
        }
        public Builder memory(String memory){
            this.memory = memory;
            return this;
        }
        public Builder disk(String disk){
            this.disk = disk;
            return this;
        }
        public Builder motherboard(String motherboard){
            this.motherboard= motherboard;
            return this;
        }

        public Computer build(){
            if (cpu == null){
                IO.println("cpu not null");
            }
            // 返回的对象不可变
            return new Computer(this);
        }
    }

     static void main(){
         // 同一构造过程起点
         final Builder base = new Builder().cpu("amd").motherboard("rog");
         final Computer office = base.memory("16g").disk("1t").build();
         final Computer game = base.memory("32g").disk("2t").build();
         // 生成不同对象
         IO.println(office);
         IO.println(game);
    }
}
