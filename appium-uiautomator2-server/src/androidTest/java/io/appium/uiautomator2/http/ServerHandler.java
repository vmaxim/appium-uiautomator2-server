/*
 * Copyright 2014 eBay Software Foundation and selendroid committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.appium.uiautomator2.http;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.appium.uiautomator2.http.impl.NettyHttpRequest;
import io.appium.uiautomator2.http.impl.NettyHttpResponse;

import io.appium.uiautomator2.util.Log;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
    private List<IHttpServlet> httpHandlers;

    public ServerHandler(List<IHttpServlet> handlers) {
        this.httpHandlers = handlers;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.i("channel read invoked!");
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().add("Connection", "close");

        Log.i("channel read: " + request.getMethod().toString() + " " + request.getUri());

        IHttpRequest httpRequest = new NettyHttpRequest(request);
        IHttpResponse httpResponse = new NettyHttpResponse(response);

        for (IHttpServlet handler : httpHandlers) {
            handler.handleHttpRequest(httpRequest, httpResponse);
            if (httpResponse.isClosed()) {
                break;
            }
        }

        if (!httpResponse.isClosed()) {
            httpResponse.setStatus(404);
            httpResponse.end();
        }

        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.log(Level.SEVERE, "Error handling request", cause);
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
