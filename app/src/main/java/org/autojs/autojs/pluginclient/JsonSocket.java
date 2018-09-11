package org.autojs.autojs.pluginclient;


import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class JsonSocket {

    private Socket mSocket;

    public JsonSocket(Socket socket) {
        mSocket = socket;

    }


    private static class SocketReader implements Runnable {

        private final Socket mSocket;
        private final InputStream mInputStream;
        private ByteBuffer mByteBuffer;
        private int mJsonDataLength = -1;
        private int mReceivedDataLength = 0;
        private byte[] mBuffer;

        private SocketReader(Socket socket) throws IOException {
            mSocket = socket;
            mInputStream = mSocket.getInputStream();
            mByteBuffer = ByteBuffer.allocateDirect(3);
        }


        @Override
        public void run() {

            try {
                readLoop();

            } catch (IOException e) {

            } finally {
            }
        }

        private void readLoop() throws IOException {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = mInputStream.read(buffer)) > 0) {
                onChunk(buffer, 0, n);
            }
        }

        private void onChunk(byte[] data, int offset, int length) {
            if(mJsonDataLength != 0){
                
            }
        }
    }


}
