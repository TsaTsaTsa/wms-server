package hse.tsantsaridi.wms.controller.http;

import hse.tsantsaridi.wms.controller.gRPC.TileClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HttpServer {
    private final int port;
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new HttpRequestDecoder(),
                                    new HttpResponseEncoder(),
                                    new HttpHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            logger.error("Shutting down HTTP server");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

