package com.example.xitong.Netty.test;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理服务端 channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)
    private  static int  i=0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // 默默地丢弃收到的数据
        System.out.println(msg.toString().length()+"长度");
        ((ByteBuf) msg).release(); // (3)
        try {
            new Thread().sleep(200);//200毫秒处理数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("计数"+(i++));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}