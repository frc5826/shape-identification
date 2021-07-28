package frc.robot;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ContourUtils {

    public List<MatOfPoint> imageToContours(byte[] image) throws IOException {
        Mat mat = imageToMat(image);
        GripPipeline pipeline = new GripPipeline();
        return pipeline.process(mat);
    }

    public Mat imageToMat(byte[] image) throws IOException {
        BufferedImage read = ImageIO.read(new ByteArrayInputStream(image));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(read, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

}
