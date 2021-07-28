package frc.robot;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.LinkedList;
import java.util.List;

public class ShapeFactory {

    private static final double MIN_AREA = 1000;
    private static final double MIN_DIST = 50;

    public List<Shape> shapesFromContours(List<MatOfPoint> contours){
        List<Shape> shapes = new LinkedList<>();
        for(MatOfPoint contour : contours){
            double area = Imgproc.contourArea(contour);
            if(area > MIN_AREA) {
                MatOfPoint2f thisContour2f = new MatOfPoint2f();
                MatOfPoint approxContour = new MatOfPoint();
                MatOfPoint2f approxContour2f = new MatOfPoint2f();

                contour.convertTo(thisContour2f, CvType.CV_32FC2);

                Imgproc.approxPolyDP(thisContour2f, approxContour2f, 0.01 * Imgproc.arcLength(thisContour2f, true), true);

                approxContour2f.convertTo(approxContour, CvType.CV_32S);

                double sides = approxContour.size().height;

                Moments m = Imgproc.moments(contour, true);
                double cx = m.m10 / m.m00;
                double cy = m.m01 / m.m00;

                Shape shape = new Shape((int) sides, cx, cy, area);

                boolean duplicate = false;

                for(Shape other : shapes){
                    duplicate = (Math.abs(shape.x - other.x) + Math.abs(shape.y - other.y)) < MIN_DIST && other.sides == shape.sides;
                    if(duplicate){
                        break;
                    }
                }

                if(!duplicate){
                    shapes.add(shape);
                }
            }
        }
        return shapes;
    }

}
