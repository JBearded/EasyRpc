package com.bj.easy.rpc.message;

import com.bj.easy.rpc.serialize.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

/**
 * @author xiejunquan
 * @create 2019/1/17 10:34
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private byte[] remainingBytes;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf currByteBuf = null;
        if(remainingBytes == null){
            currByteBuf = in;
        }else{
            //处理拆包问题
            byte[] totalBytes = new byte[remainingBytes.length + in.readableBytes()];
            System.arraycopy(remainingBytes, 0, totalBytes, 0, remainingBytes.length);
            byte[] inBytes = new byte[in.readableBytes()];
            in.readBytes(inBytes);
            System.arraycopy(inBytes, 0, totalBytes, remainingBytes.length, inBytes.length);
            currByteBuf = Unpooled.copiedBuffer(totalBytes);
        }

        while(currByteBuf.readableBytes() > 0){
            if(!doDecode(currByteBuf, out)){
                break;
            }
        }

        if(currByteBuf.readableBytes() > 0){
            remainingBytes = new byte[currByteBuf.readableBytes()];
            currByteBuf.readBytes(remainingBytes);
        }else{
            remainingBytes = null;
        }
    }

    private boolean doDecode(ByteBuf msg, List<Object> out) {
        if(msg.readableBytes() < 4)
            return false;
        msg.markReaderIndex();
        byte[] header = new byte[4];
        msg.readBytes(header);
        int len = Integer.parseInt(DatatypeConverter.printHexBinary(header), 16);
        if(msg.readableBytes() < len) {
            msg.resetReaderIndex();
            return false;
        }
        byte[] body = new byte[len];
        msg.readBytes(body);

        Message message = (Message) HessianSerializer.deserialize(body, Message.class);
        out.add(message);
        if(msg.readableBytes() > 0)
            return true;
        return false;
    }

}
