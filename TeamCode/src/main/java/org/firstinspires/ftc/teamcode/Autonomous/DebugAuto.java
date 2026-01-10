package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@Autonomous(name = "DebugAuto", group = "Autonomous")
public class DebugAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        telemetry.addLine("Checking Pinpoint...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // keep the drive's pose estimate up-to-date
            drive.updatePoseEstimate();

            // safe-cast to PinpointLocalizer only if it's actually in use
            if (drive.localizer instanceof PinpointLocalizer) {
                PinpointLocalizer pin = (PinpointLocalizer) drive.localizer;

                telemetry.addData("Pinpoint status", pin.driver.getDeviceStatus());
                telemetry.addData("Pin X (in)", pin.driver.getPosX(DistanceUnit.INCH));
                telemetry.addData("Pin Y (in)", pin.driver.getPosY(DistanceUnit.INCH));
                telemetry.addData("Pin Heading (deg)",
                        Math.toDegrees(pin.driver.getHeading(UnnormalizedAngleUnit.RADIANS)));
            } else {
                telemetry.addLine("PinpointLocalizer not present on drive.localizer");
            }

            // IMU yaw (from LazyImu) and motor encoder ticks
            telemetry.addData("IMU yaw (deg)",
                    Math.toDegrees(drive.lazyImu.get().getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS)));

            // encoder ticks (raw)
            telemetry.addData("LF ticks", drive.leftFront.getCurrentPosition());
            telemetry.addData("LB ticks", drive.leftBack.getCurrentPosition());
            telemetry.addData("RF ticks", drive.rightFront.getCurrentPosition());
            telemetry.addData("RB ticks", drive.rightBack.getCurrentPosition());

            // motor power check
            telemetry.addData("LeftFront Power", drive.leftFront.getPower());

            telemetry.update();

            // small manual test action when you press 'A'
            if (gamepad1.a) {
                Actions.runBlocking(drive.actionBuilder(startPose)
                        .lineToX(10)
                        .build());
            }
        }
    }
}
