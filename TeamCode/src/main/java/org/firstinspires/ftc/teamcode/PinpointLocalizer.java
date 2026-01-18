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
        // Y pod offset (forward/back from center) in *ticks*
        public double parYTicks = 874;

        // X pod offset (left/right from center) in *ticks*
        // Left is +, right is -
        public double perpXTicks = -2545;  // you said negative worked better
    }

    public static Params PARAMS = new Params();

    public final GoBildaPinpointDriver driver;
    public final GoBildaPinpointDriver.EncoderDirection initialParDirection, initialPerpDirection;

    private Pose2d txWorldPinpoint;
    private Pose2d txPinpointRobot = new Pose2d(0, 0, 0);

    public PinpointLocalizer(HardwareMap hardwareMap, double inPerTick, Pose2d initialPose) {
        driver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        double mmPerTick = inPerTick * 25.4;

        // Set encoder resolution in ticks/mm
        driver.setEncoderResolution(1 / mmPerTick, DistanceUnit.MM);

        // Apply offsets in mm
        driver.setOffsets(mmPerTick * PARAMS.parYTicks, mmPerTick * PARAMS.perpXTicks, DistanceUnit.MM);

        // Encoder directions (flip if needed)
        initialParDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        initialPerpDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        driver.setEncoderDirections(initialParDirection, initialPerpDirection);

        // IMPORTANT: robot must be still when this runs
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
            // IMPORTANT:
            // Road Runner expects: X = forward, Y = left
            // If your robot forward makes Y increase and left makes X increase, swap X/Y here.
            double rrX = -driver.getPosY(DistanceUnit.INCH);
            double rrY = driver.getPosX(DistanceUnit.INCH);

            double heading = -driver.getHeading(UnnormalizedAngleUnit.RADIANS);

            txPinpointRobot = new Pose2d(rrX, rrY, heading);

            Vector2d worldVelocity = new Vector2d(
                    driver.getVelY(DistanceUnit.INCH),
                    driver.getVelX(DistanceUnit.INCH)
            );

            Vector2d robotVelocity = Rotation2d.fromDouble(-txPinpointRobot.heading.log()).times(worldVelocity);

            return new PoseVelocity2d(robotVelocity, driver.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS));
        }

        return new PoseVelocity2d(new Vector2d(0, 0), 0);
    }
}
