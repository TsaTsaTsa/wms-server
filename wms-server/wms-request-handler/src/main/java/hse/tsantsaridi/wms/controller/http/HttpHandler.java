package hse.tsantsaridi.wms.controller.http;

import hse.tsantsaridi.wms.logic.getCapabilities.GetCapabilitiesManager;
import hse.tsantsaridi.wms.logic.getMap.GetMapManager;
import hse.tsantsaridi.wms.metrics.MetricsHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import wms.TileServiceOuterClass;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final GetCapabilitiesManager gcm = new GetCapabilitiesManager();
    private final GetMapManager gm = new GetMapManager();
    private final String GET_CAPABILITIES_REQUEST = "GetCapabilities";
    private final String GET_MAP_REQUEST = "GetMap";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest request) {
            if (request.uri().contains("/metrics")) {
                String metrics = MetricsHolder.PROM_REGISTRY.scrape();

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        ctx.alloc().buffer().writeBytes(metrics.getBytes())
                );

                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                MetricsHolder.incInFlight();
                long start = System.nanoTime();

                String uri = request.uri();
                HttpResponseStatus status = HttpResponseStatus.OK;
                byte[] responseContent = new byte[0];
                AsciiString responseType = HttpHeaderValues.DEFLATE;

                try {
                    if (uri.contains(GET_CAPABILITIES_REQUEST)) {
                        responseContent = getCapabilitiesResponse();
                        responseType = HttpHeaderValues.APPLICATION_XML;
                        MetricsHolder.HTTP_GET_CAPABILITY_2XX.increment();
                    } else if (uri.contains(GET_MAP_REQUEST)) {
                        responseContent = getMapResponse(uri).getImage().toByteArray();
                        responseType = AsciiString.of(getResponseType(responseContent));
                        MetricsHolder.HTTP_GET_MAP_2XX.increment();
                    }
                } catch (Exception ex) {
                    responseContent = ("Error: " + ex.getMessage()).getBytes();
                    responseType = HttpHeaderValues.TEXT_PLAIN;
                    status = HttpResponseStatus.INTERNAL_SERVER_ERROR;

                    if (uri.contains(GET_MAP_REQUEST)) MetricsHolder.HTTP_GET_MAP_5XX.increment();
                    else MetricsHolder.HTTP_GET_CAPABILITY_5XX.increment();
                }

                MetricsHolder.HTTP_LATENCY.record(System.nanoTime() - start, java.util.concurrent.TimeUnit.NANOSECONDS);
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, status,
                        Unpooled.wrappedBuffer(responseContent));

                response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseType);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                MetricsHolder.decInFlight();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private byte[] getCapabilitiesResponse() throws IOException, SQLException {
        return gcm.getMetadata();
    }

    private TileServiceOuterClass.TileResponse getMapResponse(String uri) throws SQLException {
        return gm.getResponse(uri).getFirst();
    }

    private String getResponseType(byte[] responseContent) {
        if (Arrays.toString(responseContent).startsWith("{")) {
            return "application/json";
        } else {
            return "image/png";
        }
    }
}


