package com;

import com.server.NettyServer;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "Hello World!" );
        //启动server服务
        new NettyServer().bind(8080);
    }
}
