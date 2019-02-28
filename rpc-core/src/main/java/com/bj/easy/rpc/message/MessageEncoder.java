package com.bj.easy.rpc.message;

import com.bj.easy.rpc.serialize.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @author xiejunquan
 * @create 2019/1/17 10:34
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] body = HessianSerializer.serialize(msg);
        byte[] header = ByteBuffer.allocate(4).putInt(body.length).array();
        byte[] data = new byte[header.length + body.length];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(body, 0, data, header.length, body.length);
        out.writeBytes(data);
    }
}
