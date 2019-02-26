package us.kpm.rpc.client;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kpm.rpc.common.SelinusRpcDecoder;
import us.kpm.rpc.common.SelinusRpcEncoder;
import us.kpm.rpc.common.SelinusRpcRequest;
import us.kpm.rpc.common.SelinusRpcResponse;
import us.kpm.rpc.exception.RpcDiscoverException;
import us.kpm.rpc.register.SelinusRpcDiscover;

/**
 * Description: SelinusRpcClient
 * PackageName: us.kpm.rpc.client
 *
 * @author: chenglulu
 * @version: 1.0
 * Filename:    SelinusRpcClient.java
 * Create at:  2019/2/26
 * Copyright:   Copyright (c)2019
 */
public class SelinusRpcClient extends SimpleChannelInboundHandler<SelinusRpcResponse> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SelinusRpcClient.class);


    /**
     * 请求
     */
    private SelinusRpcRequest request;

    /**
     * 返回
     */
    private SelinusRpcResponse response;

    /**
     * 用于获取服务地址列表信息
     */
    private SelinusRpcDiscover selinusRpcDiscover;


    /**
     * 同步锁 资源对象
     */
    private Object o = new Object();


    public SelinusRpcClient(SelinusRpcRequest request, SelinusRpcDiscover selinusRpcDiscover){
        this.request = request;
        this.selinusRpcDiscover = selinusRpcDiscover;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SelinusRpcResponse msg) throws Exception {

        //响应消息
        this.response = msg;

        synchronized (o){
            //刷新缓存
            ctx.flush();
            //唤醒等待
            o.notifyAll();
        }
    }

    /**
     * 发送信息
     * @return
     */
    public SelinusRpcResponse send(){

        //创建一个socket通信对象
        Bootstrap client = new Bootstrap();
        //创建一个通信组，负责Channel(通道)的I/O事件的处理
        NioEventLoopGroup  worker = new NioEventLoopGroup();

        try {
            client.group(worker)
                     //使用异步的socket通信
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            //初始化
                            channel.pipeline()
                                    .addLast(new SelinusRpcEncoder(SelinusRpcRequest.class))
                                    .addLast(new SelinusRpcDecoder(SelinusRpcResponse.class))
                                    //发送请求对象
                                    .addLast(SelinusRpcClient.this);

                        }})
                    .option(ChannelOption.SO_KEEPALIVE,true);

            //获取服务地址
            String discoverRpcAddress = selinusRpcDiscover.discover();
            //获取服务host
            String host = discoverRpcAddress.split(":")[0];
            //获取端口
            Integer port = Integer.valueOf(discoverRpcAddress.split(":")[1]);
            //建立连接
            ChannelFuture channelFuture = client.connect(host, port).sync();
            //发送数据
            LOGGER.info("客户端准备发送的数据，request:{}", JSON.toJSONString(request));
            channelFuture.channel().writeAndFlush(selinusRpcDiscover).sync();
            //线程控制
            synchronized (o){
                o.wait();
            }
            if(response != null){
                //等待服务端关闭socket
                channelFuture.channel().closeFuture().sync();
            }

        } catch (Exception e) {
            LOGGER.error("SelinusRpcClient.send error,e:{}",e);
        }finally {
            worker.shutdownGracefully();
        }
        return response;
    }

    /**
     * 重写异常捕捉 关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
