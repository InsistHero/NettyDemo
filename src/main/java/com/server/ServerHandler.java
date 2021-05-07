package com.server;

import com.netty.common.BusinessData;
import com.netty.common.RpcRequest;
import com.netty.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Override
    //接受client发送的消息
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final RpcRequest request = (RpcRequest) msg;
        System.out.println("接收到客户端信息:" + request.toString());

        POOL_EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                // 获取处理结果
                RpcResponse rpcResponse = processRpcRequest(request);
                // write response
                writeResponse(ctx, rpcResponse);
            }
        });
    }


    @Override
    //通知处理器最后的channelRead()是当前批处理中的最后一条消息时调用
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端接收数据完毕..");
        ctx.flush();
    }

    @Override
    //读操作时捕获到异常时调用
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    private RpcResponse processRpcRequest(RpcRequest request) {
        //返回的数据结构
        if (request.getUrl().equals("/rpc/invoke")) {
            RpcResponse response = new RpcResponse();
            response.setId(UUID.randomUUID().toString());

            BusinessData businessData = new BusinessData();
            businessData.setBusinessId(UUID.randomUUID().getLeastSignificantBits());
            businessData.setBusinessCode("method businessCode");
            businessData.setReason("调用成功,返回处理结果");

            response.setData(businessData);
            response.setStatus(200);
            return response;
        }
        return new RpcResponse("9999", null, -1);
    }

    private void writeResponse(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        ctx.writeAndFlush(rpcResponse);
    }
}
