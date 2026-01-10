//package org.firstinspires.ftc.teamcode;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.PoseVelocity2d;
//import com.acmerobotics.roadrunner.Rotation2d;
//import com.acmerobotics.roadrunner.Vector2d;
//import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
//
//import java.util.Objects;
//
//@Config
//public final class PinpointLocalizer implements Localizer {
//    public static class Params {
//        public double parYTicks = 0.0; // y position of the parallel encoder (in tick units)
//        public double perpXTicks = 0.0; // x position of the perpendicular encoder (in tick units)
//    }
//
//    public static Params PARAMS = new Params();
//
//    public final GoBildaPinpointDriver driver;
//    // Remove 'final' so the compiler doesn't complain, or just initialize them
//    public GoBildaPinpointDriver.EncoderDirection initialParDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
//    public GoBildaPinpointDriver.EncoderDirection initialPerpDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
//    private Pose2d txWorldPinpoint;
//    private Pose2d txPinpointRobot = new Pose2d(0, 0, 0);
//
//    public PinpointLocalizer(HardwareMap hardwareMap, double inPerTick, Pose2d initialPose) {
//        driver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
//
//        // inPerTick for 48mm wheels is ~0.0014497
//        double mmPerTick = inPerTick * 25.4;
//        driver.setEncoderResolution(1 / mmPerTick, DistanceUnit.MM);
//
//        // Measure your offsets in mm! 0,0 will work for testing but may drift.
//        driver.setOffsets(0, 0, DistanceUnit.MM);
//
//        // Directions: If the robot thinks it's going backwards, change one to REVERSE
//        driver.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
//                GoBildaPinpointDriver.EncoderDirection.FORWARD);
//
//        // This resets the IMU. MUST BE DONE WHILE ROBOT IS FLAT AND STILL.
//        driver.resetPosAndIMU();
//
//        txWorldPinpoint = initialPose;
//    }
//
//    @Override
//    public void setPose(Pose2d pose) {
//        txWorldPinpoint = pose.times(txPinpointRobot.inverse());
//    }
//
//    @Override
//    public Pose2d getPose() {
//        return txWorldPinpoint.times(txPinpointRobot);
//    }
//
//    @Override
//    public PoseVelocity2d update() {
//        driver.update();
//        if (Objects.requireNonNull(driver.getDeviceStatus()) == GoBildaPinpointDriver.DeviceStatus.READY) {
//            txPinpointRobot = new Pose2d(driver.getPosX(DistanceUnit.INCH), driver.getPosY(DistanceUnit.INCH), driver.getHeading(UnnormalizedAngleUnit.RADIANS));
//            Vector2d worldVelocity = new Vector2d(driver.getVelX(DistanceUnit.INCH), driver.getVelY(DistanceUnit.INCH));
//            Vector2d robotVelocity = Rotation2d.fromDouble(-txPinpointRobot.heading.log()).times(worldVelocity);
//
//            return new PoseVelocity2d(robotVelocity, driver.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS));
//        }
//        return new PoseVelocity2d(new Vector2d(0, 0), 0);
//    }
//}





package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

import java.util.Objects;

@Config
public final class PinpointLocalizer implements Localizer {
    public static class Params {
        // original pod offset placeholders (tick units in your previous code; kept for compatibility)
        public double parYTicks = 0.0; // y position of the parallel encoder (in tick units)
        public double perpXTicks = 0.0; // x position of the perpendicular encoder (in tick units)

        // new tuning helpers (edit these live while testing)
        public double headingOffsetDeg = 0.0; // add a constant heading offset (degrees)
        public boolean swapXY = false;        // set true if Pin X and Pin Y are swapped
        public boolean flipX = false;         // set true to flip X sign
        public boolean flipY = false;         // set true to flip Y sign
    }

    public static Params PARAMS = new Params();

    public final GoBildaPinpointDriver driver;
    // initial directions - set to FORWARD initially and flip in code if needed
    public GoBildaPinpointDriver.EncoderDirection initialParDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    public GoBildaPinpointDriver.EncoderDirection initialPerpDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    private Pose2d txWorldPinpoint;
    private Pose2d txPinpointRobot = new Pose2d(0, 0, 0);

    /**
     * @param hardwareMap hardwareMap from opmode
     * @param inPerTick   inches per tick (this must be accurate for good scale)
     * @param initialPose the initial world pose
     */
    public PinpointLocalizer(HardwareMap hardwareMap, double inPerTick, Pose2d initialPose) {
        driver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // inPerTick -> mm conversion used by the driver
        double mmPerTick = inPerTick * 25.4;
        // driver expects encoder resolution in ticks per mm:
        driver.setEncoderResolution(1.0 / mmPerTick, DistanceUnit.MM);

        // Set initial encoder directions; toggle these if you observe sign inversions:
        driver.setEncoderDirections(initialParDirection, initialPerpDirection);

        // Set pod offsets (in mm). Keep as 0 for initial tests but set approximate measured offsets later.
        driver.setOffsets(0, 0, DistanceUnit.MM);

        // IMPORTANT: reset after configuring directions and offsets
        driver.resetPosAndIMU();

        txWorldPinpoint = initialPose;
    }

    @Override
    public void setPose(Pose2d pose) {
        txWorldPinpoint = pose.times(txPinpointRobot.inverse());
    }

    @Override
    public Pose2d getPose() {
        return txWorldPinpoint.times(txPinpointRobot);
    }

    @Override
    public PoseVelocity2d update() {
        driver.update();
        if (Objects.requireNonNull(driver.getDeviceStatus()) == GoBildaPinpointDriver.DeviceStatus.READY) {
            // read raw values from the driver
            double rawX = driver.getPosX(DistanceUnit.INCH);
            double rawY = driver.getPosY(DistanceUnit.INCH);
            double rawHeading = driver.getHeading(UnnormalizedAngleUnit.RADIANS);

            // Apply configurable swap/flips
            double xVal = PARAMS.swapXY ? rawY : rawX;
            double yVal = PARAMS.swapXY ? rawX : rawY;
            if (PARAMS.flipX) xVal = -xVal;
            if (PARAMS.flipY) yVal = -yVal;

            // Apply heading offset (convert degrees -> radians)
            double headingVal = rawHeading + Math.toRadians(PARAMS.headingOffsetDeg);

            txPinpointRobot = new Pose2d(xVal, yVal, headingVal);

            Vector2d worldVelocity = new Vector2d(driver.getVelX(DistanceUnit.INCH), driver.getVelY(DistanceUnit.INCH));
            Vector2d robotVelocity = Rotation2d.fromDouble(-txPinpointRobot.heading.log()).times(worldVelocity);

            return new PoseVelocity2d(robotVelocity, driver.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS));
        }
        // Not ready: return zero velocity
        return new PoseVelocity2d(new Vector2d(0, 0), 0);
    }
}
