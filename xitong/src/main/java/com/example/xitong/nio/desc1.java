package com.example.xitong.nio;

public class desc1 {
}
/** 例如搬苹果
 * NIO主要有三大核心部分：Channel(通道)，Buffer(缓冲区), Selector。
 * 传统IO基于字节流（一个一个搬）和字符流（一箱一箱搬）进行操作，而NIO基于Channel和Buffer(缓冲区)（一车一车搬）进行操作，
 * 数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。
 * Selector(选择区)用于监听多个通道的事件（比如：连接打开，数据到达）。因此，单个线程可以监听多个数据通道。
 * */
/**
 * 首先说一下Channel，国内大多翻译成“通道”。Channel和IO中的Stream(流)是差不多一个等级的。
 * 只不过Stream是单向的，譬如：InputStream, OutputStream.而Channel是双向的，既可以用来进行读操作，又可以用来进行写操作。
 * NIO中的Channel的主要实现有：
 * * FileChannel
 * * DatagramChannel
 * * SocketChannel
 * * ServerSocketChannel
 * * 这里看名字就可以猜出个所以然来：分别可以对应文件IO、UDP和TCP（Server和Client）。下面演示的案例基本上就是围绕这4个类型的Channel进行陈述的。
 * */
/**
 * NIO中的关键Buffer实现有：
 * ByteBuffer, CharBuffer, DoubleBuffer, FloatBuffer, IntBuffer, LongBuffer, ShortBuffer，
 * 分别对应基本数据类型:
 * byte, char, double, float, int, long, short。
 * 当然NIO中还有MappedByteBuffer, HeapByteBuffer, DirectByteBuffer等。
 * */
/**
 *Selector类可以用于避免使用阻塞式客户端中很浪费资源的“忙等”方法。
 * 例如，考虑一个IM服务器。像QQ或者旺旺这样的，可能有几万甚至几千万个客户端同时连接到了服务器，但在任何时刻都只是非常少量的消息。
 *
 * 需要读取和分发。这就需要一种方法阻塞等待，直到至少有一个信道可以进行I/O操作，并指出是哪个信道。
 * NIO的选择器就实现了这样的功能。一个Selector实例可以同时检查一组信道的I/O状态。
 * 用专业术语来说，选择器就是一个多路开关选择器，因为一个选择器能够管理多个信道上的I/O操作。
 * 然而如果用传统的方式来处理这么多客户端，使用的方法是循环地一个一个地去检查所有的客户端是否有I/O操作，
 * 如果当前客户端有I/O操作，则可能把当前客户端扔给一个线程池去处理，如果没有I/O操作则进行下一个轮询，
 * 当所有的客户端都轮询过了又接着从头开始轮询；这种方法是非常笨而且也非常浪费资源，因为大部分客户端是没有I/O操作，
 * 我们也要去检查；而Selector就不一样了，它在内部可以同时管理多个I/O，当一个信道有I/O操作的时候，
 * 他会通知Selector，Selector就是记住这个信道有I/O操作，并且知道是何种I/O操作，是读呢？是写呢？
 * 还是接受新的连接；所以如果使用Selector，它返回的结果只有两种结果，一种是0，即在你调用的时刻没有任何客户端需要I/O操作，
 * 另一种结果是一组需要I/O操作的客户端，这时你就根本不需要再检查了，因为它返回给你的肯定是你想要的。
 * 这样一种通知的方式比那种主动轮询的方式要高效得多！
 *
 * 要使用选择器（Selector），需要创建一个Selector实例（使用静态工厂方法open()）并将其注册（register）
 * 到想要监控的信道上（注意，这要通过channel的方法实现，而不是使用selector的方法）。
 * 最后，调用选择器的select()方法。该方法会阻塞等待，直到有一个或更多的信道准备好了I/O操作或等待超时。
 * select()方法将返回可进行I/O操作的信道数量。现在，在一个单独的线程中，通过调用select()方法就能检查多个信道是否准备好进行I/O操作。
 * 如果经过一段时间后仍然没有信道准备好，select()方法就会返回0，并允许程序继续执行其他任务。
 * */
