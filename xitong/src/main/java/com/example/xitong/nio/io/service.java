package com.example.xitong.nio.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * nio方式的服务端
 * */

/**
 * 在ByteBuffer中，读写指针都是position，而在ByteBuf中，读写指针分别为readerIndex和writerIndex，
 * 直观看上去ByteBuffer仅用了一个指针就实现了两个指针的功能，节省了变量，
 *
 * 但是当对于ByteBuffer的读写状态切换的时候必须要调用flip方法，而当下一次写之前，
 * 必须要将Buffe中的内容读完，再调用clear方法。每次读之前调用flip，写之前调用clear，
 * 这样无疑给开发带来了繁琐的步骤，而且内容没有读完是不能写的，这样非常不灵活。
 *
 * 相比之下我们看看ByteBuf（netty），读的时候仅仅依赖readerIndex指针，
 * 写的时候仅仅依赖writerIndex指针，不需每次读写之前调用对应的方法，而且没有必须一次读完的限制
 * */
public class service {
    private static final int BUF_SIZE=1024;
    private static final int PORT = 8080;
    private static final int TIMEOUT = 3000;
    public static void main(String[] args)
    {
        selector();
    }
    public static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
    }
    public static void handleRead(SelectionKey key) throws IOException{
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer buf = (ByteBuffer)key.attachment();
        long bytesRead = sc.read(buf);
        while(bytesRead>0){
            buf.flip();
            while(buf.hasRemaining()){
                System.out.print((char)buf.get());
            }
            System.out.println();
            buf.clear();
            bytesRead = sc.read(buf);
        }
        if(bytesRead == -1){
            sc.close();
        }
    }
    public static void handleWrite(SelectionKey key) throws IOException{
        ByteBuffer buf = (ByteBuffer)key.attachment();
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while(buf.hasRemaining()){
            sc.write(buf);
        }
        buf.compact();
    }
    public static void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        try{
            selector = Selector.open();
            ssc= ServerSocketChannel.open();//打开
            ssc.socket().bind(new InetSocketAddress(PORT));//绑定
            ssc.configureBlocking(false);//非阻塞
            /*为了将Channel和Selector配合使用，必须将Channel注册到Selector上，通过SelectableChannel.register()方法来实现*/
            /*通道触发了一个事件意思是该事件已经就绪。
            所以，某个channel成功连接到另一个服务器称为“连接就绪”（SelectionKey.OP_CONNECT）。
            一个server socket channel准备好接收新进入的连接称为“接收就绪”（SelectionKey.OP_ACCEPT）。
            一个有数据可读的通道可以说是“读就绪”（SelectionKey.OP_READ）。
            等待写数据的通道可以说是“写就绪”（SelectionKey.OP_WRITE）。
            */
            ssc.register(selector, SelectionKey.OP_ACCEPT);//接受就绪

            while(true){
                if(selector.select(TIMEOUT) == 0){//最长阻塞时间，如果阻塞时间等于0，说明没有连接
                    System.out.println("==");
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();//调用通道集合
                while(iter.hasNext()){
                    SelectionKey key = iter.next();//获取到这个连接通道key
                    if(key.isAcceptable()){
                        System.out.println("isAccept = true");
                        handleAccept(key);
                    }
                    if(key.isReadable()){
                        System.out.println("isRead = true");
                        handleRead(key);
                    }
                    if(key.isWritable() && key.isValid()){
                        System.out.println("isWrite = true");
                        handleWrite(key);
                    }
                    if(key.isConnectable()){
                        System.out.println("isConnectable = true");
                    }
                    iter.remove();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(selector!=null){
                    selector.close();//关闭
                }
                if(ssc!=null){
                    ssc.close();//关闭
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
