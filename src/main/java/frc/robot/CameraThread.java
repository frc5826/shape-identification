package frc.robot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//https://github.com/Team1100/FRCPowerUp/blob/d1f435bd85989d806aa096ae07d3e4f3d2c20c2d/src/org/usfirst/frc/team1100/robot/commands/vision/SaveCubePNG.java
public class CameraThread extends Thread {

    private static final int[] START_BYTES = new int[]{0xFF, 0xD8};
    private static final int[] END_BYTES = new int[]{0xFF, 0xD9};
    private static final String STREAM_PREFIX = "mjpg:";
    public final String url = STREAM_PREFIX + "http://limelight.local:5802";
    private InputStream stream;
    private Consumer<byte[]> consumer;

    public CameraThread(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    public void close()  {
        if(stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        close();
        super.interrupt();
    }

    /**
     * Runs thread. Detects if image should be saved, saves it if so.
     * @return
     */
    public void run() {
        while(!interrupted()) {
            try {
                if (stream == null) {
                    stream = createCameraStream();
                }
                ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
                stream.skip(stream.available());
                imageBuffer.reset();
                readUntil(stream, START_BYTES);
                Arrays.stream(START_BYTES).forEachOrdered(imageBuffer::write);
                readUntil(stream, END_BYTES, imageBuffer);
                consumer.accept(imageBuffer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    /**
     * Gets all streams
     * @return Stream object
     */
    public Stream<String> streamPossibleCameraUrls() {
        return Stream.of(url);
    }

    /**
     * Gets camera stream
     * @return Camera stream
     */
    private InputStream createCameraStream() throws IOException {
        InputStream stream = null;
        for (String streamUrl : streamPossibleCameraUrls()
                .filter(s -> s.startsWith(STREAM_PREFIX))
                .map(s -> s.substring(STREAM_PREFIX.length()))
                .collect(Collectors.toSet())) {
            if(!interrupted()) {
                System.out.println("Trying to connect to: " + streamUrl);
                URL url = new URL(streamUrl);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(500);
                connection.setReadTimeout(5000);
                stream = connection.getInputStream();
                System.out.println("Connected to: " + streamUrl);
            }
        }
        return stream;
    }


    private void readUntil(InputStream stream, int[] bytes) throws IOException {
        readUntil(stream, bytes, null);
    }

    private void readUntil(InputStream stream, int[] bytes, ByteArrayOutputStream buffer)
            throws IOException {
        for (int i = 0; i < bytes.length; ) {
            if(!interrupted()) {
                int b = stream.read();
                if (b == -1) {
                    throw new IOException("End of Stream reached");
                }
                if (buffer != null) {
                    buffer.write(b);
                }
                if (b == bytes[i]) {
                    i++;
                } else {
                    i = 0;
                }
            }
        }
    }

}