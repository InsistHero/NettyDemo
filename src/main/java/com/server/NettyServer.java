package com.server;

import com.netty.common.RpcDecoder;
import com.netty.common.RpcEncoder;
import com.netty.common.RpcRequest;
import com.netty.common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public void bind(int port) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // //初始化服务端可连接队列,指定了队列的大小128
                .option(ChannelOption.SO_BACKLOG, 128)
                // option主要是针对boss线程组，child主要是针对worker线程组
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                // 绑定客户端连接时候触发操作
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RpcDecoder(RpcRequest.class)) //解码request
                                .addLast(new RpcEncoder(RpcResponse.class)) //编码response
                                .addLast(new ServerHandler()); //使用ServerHandler类来处理接收到的消息
                    }
                });

        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完
        ChannelFuture future = serverBootstrap.bind(port).sync();

        if(future.isSuccess()){
            System.out.println("服务器启动成功～～～");
        }else{
            System.out.println("服务器启动失败～～～");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
