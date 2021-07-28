package frc.robot;

import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ContourThread extends Thread implements Consumer<byte[]> {

    private ContourUtils cu = new ContourUtils();
    private ShapeFactory sf = new ShapeFactory();
    private List<Shape> shapes;
    private byte[] image;
    private AtomicBoolean imageUpdated = new AtomicBoolean(false);
    private AtomicBoolean shapesUpdated = new AtomicBoolean(false);

    @Override
    public void accept(byte[] bytes) {
        this.image = bytes;
        imageUpdated.set(true);
    }

    public List<Shape> getShapes() {
        shapesUpdated.set(false);
        return shapes;
    }

    public boolean isUpdated(){
        return shapes != null && shapesUpdated.get();
    }

    @Override
    public void run() {
        while(!interrupted()){
            if(imageUpdated.get()) {
                try {
                    List<MatOfPoint> imageContours = cu.imageToContours(image);
                    imageUpdated.set(false);
                    shapes = sf.shapesFromContours(imageContours);
                    shapesUpdated.set(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
