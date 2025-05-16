package hse.tsantsaridi.wms.controller.http;

import hse.tsantsaridi.wms.controller.gRPC.TileClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    private final GetCapabilitiesManager gcm = new GetCapabilitiesManager();
    private final GetMapManager gm = new GetMapManager();
    private static final String GET_CAPABILITIES_REQUEST = "GetCapabilities";
    private static final String GET_MAP_REQUEST = "GetMap";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (!(msg instanceof HttpRequest request)) {
            return;
        }

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        String uri = request.uri();
        logger.info("[{}] Incoming HTTP request: {} {}", requestId, request.method(), uri);

        try {
            if (uri.contains("/metrics")) {
                logger.debug("[{}] Handling metrics endpoint", requestId);

                String metrics = MetricsHolder.PROM_REGISTRY.scrape();
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        ctx.alloc().buffer().writeBytes(metrics.getBytes())
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

                logger.info("[{}] Metrics response sent (size={})", requestId, metrics.length());
            } else {
                MetricsHolder.incInFlight();
                long start = System.nanoTime();
                HttpResponseStatus status = HttpResponseStatus.OK;
                byte[] responseContent;
                AsciiString responseType;

                try {
                    if (uri.contains(GET_CAPABILITIES_REQUEST)) {
                        logger.info("[{}] Processing GetCapabilities request", requestId);

                        responseContent = getCapabilitiesResponse();
                        responseType = HttpHeaderValues.APPLICATION_XML;

                        MetricsHolder.HTTP_GET_CAPABILITY_2XX.increment();
                        logger.debug("[{}] Capabilities response length: {} bytes", requestId, responseContent.length);
                    } else if (uri.contains(GET_MAP_REQUEST)) {
                        logger.info("[{}] Processing GetMap request", requestId);

                        TileServiceOuterClass.TileResponse tileResponse = getMapResponse(uri);
                        responseContent = tileResponse.getImage().toByteArray();
                        responseType = AsciiString.of(getResponseType(responseContent));

                        MetricsHolder.HTTP_GET_MAP_2XX.increment();
                        logger.debug("[{}] Map response byte length: {}", requestId, responseContent.length);
                    } else {
                        logger.warn("[{}] Unsupported URI: {}", requestId, uri);

                        responseContent = new byte[0];
                        responseType = HttpHeaderValues.TEXT_PLAIN;
                        status = HttpResponseStatus.NOT_FOUND;
                    }
                } catch (Exception ex) {
                    logger.error("[{}] Error processing request {}", requestId, uri, ex);
                    responseContent = ("Error: " + ex.getMessage()).getBytes();
                    responseType = HttpHeaderValues.TEXT_PLAIN;
                    status = HttpResponseStatus.INTERNAL_SERVER_ERROR;

                    if (uri.contains(GET_MAP_REQUEST)) {
                        MetricsHolder.HTTP_GET_MAP_5XX.increment();
                    } else {
                        MetricsHolder.HTTP_GET_CAPABILITY_5XX.increment();
                    }
                }

                long latency = System.nanoTime() - start;
                MetricsHolder.HTTP_LATENCY.record(latency, java.util.concurrent.TimeUnit.NANOSECONDS);
                logger.info("[{}] Request {} completed with status {} in {} ns", requestId, uri, status.code(), latency);

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, status,
                        Unpooled.wrappedBuffer(responseContent)
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseType);
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                MetricsHolder.decInFlight();
            }
        } finally {
            MDC.remove("requestId");
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
