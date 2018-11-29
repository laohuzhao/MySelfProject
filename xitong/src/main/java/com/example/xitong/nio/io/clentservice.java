package com.example.xitong.nio.io;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * SocketChannel的用法
 * NIO的强大功能部分来自于Channel的非阻塞特性，套接字的某些操作可能会无限期地阻塞。
 * 例如，对accept()方法的调用可能会因为等待一个客户端连接而阻塞；
 * 对read()方法的调用可能会因为没有数据可读而阻塞，直到连接的另一端传来新的数据。
 * 总的来说，创建/接收连接或读写数据等I/O调用，都可能无限期地阻塞等待，直到底层的网络实现发生了什么。
 * 慢速的，有损耗的网络，或仅仅是简单的网络故障都可能导致任意时间的延迟。
 * 然而不幸的是，在调用一个方法之前无法知道其是否阻塞。
 * NIO的channel抽象的一个重要特征就是可以通过配置它的阻塞行为，以实现非阻塞式的信道。
 *     channel.configureBlocking(false)
 * */
public class clentservice {
    /*nio实现客户端*/
    public static void client(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = null;
        try
        {
            socketChannel = SocketChannel.open();//打开通道
            socketChannel.configureBlocking(false);//设置非阻塞方式
            socketChannel.connect(new InetSocketAddress("127.0.0.1",8080));//连接ip
            if(socketChannel.finishConnect())
            {
                int i=0;
                while(true)
                {
                    TimeUnit.SECONDS.sleep(1);
                    String info = "I'm "+i+++"-th information from client";
                    buffer.clear();
                    buffer.put(info.getBytes());//读取数据
                    //读取模式
                    buffer.flip();//调用实际数据
                    while(buffer.hasRemaining()){
                        socketChannel.write(buffer);//发送数据
                    }
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        finally{
            try{
                if(socketChannel!=null){
                    socketChannel.close();//关闭通道
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        client();
    }
}
