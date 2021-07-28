// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * This is a demo program showing the use of OpenCV to do vision processing. The
 * image is acquired from the USB camera, then a rectangle is put on the image
 * and sent to the dashboard. OpenCV has many methods for different types of
 * processing.
 * <p>
 * The VM is configured to automatically run this class, and to call the
 * methods corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot
{
    static {
        System.load("/usr/local/frc/third-party/lib/libopencv_java347.so");
    }

    private CameraThread camera;
    private ContourThread contour;
    private Instant lastUpdate;

    @Override
    public void robotInit(){

    }

    @Override
    public void autonomousInit() {
        contour = new ContourThread();
        contour.start();
        camera = new CameraThread(contour);
        camera.start();
    }

    @Override
    public void disabledInit() {
        if(camera != null) {
            camera.interrupt();
        }
        camera = null;
        if(contour != null) {
            contour.interrupt();
        }
        contour = null;
    }

    @Override
    public void autonomousPeriodic() {
        if(contour.isUpdated()) {
            List<Shape> shapes = contour.getShapes();
            System.out.println("Shapes: " + shapes);
            if(lastUpdate != null){
                System.out.println("Update Time: " + Duration.between(lastUpdate, Instant.now()).toMillis() + "ms");
            }
            lastUpdate = Instant.now();
        }
    }

}
