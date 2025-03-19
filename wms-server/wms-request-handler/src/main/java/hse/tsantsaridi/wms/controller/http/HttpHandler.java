package hse.tsantsaridi.wms.controller.http;

import hse.tsantsaridi.wms.logic.getCapabilities.GetCapabilitiesManager;
import hse.tsantsaridi.wms.logic.getMap.GetMapManager;
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
            String uri = request.uri();
            byte[] responseContent = new byte[0];
            AsciiString responseType = HttpHeaderValues.DEFLATE;

            if (uri.contains(GET_CAPABILITIES_REQUEST)) {
                responseContent = getCapabilitiesResponse();
                responseType = HttpHeaderValues.APPLICATION_XML;
            } else if (uri.contains(GET_MAP_REQUEST)) {
                responseContent = getMapResponse(uri).getImage().toByteArray();
                responseType = AsciiString.of(getResponseType(responseContent));
            }

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseType);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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


