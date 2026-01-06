package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;

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
            drive.updatePoseEstimate();
            Pose2d currentPose = drive.localizer.getPose();

            // If these numbers don't change when you PUSH the robot,
            // the Pinpoint is not configured correctly.
            telemetry.addData("X Pose", currentPose.position.x);
            telemetry.addData("Y Pose", currentPose.position.y);
            telemetry.addData("Heading", Math.toDegrees(currentPose.heading.toDouble()));

            // Check if motors have power
            telemetry.addData("LeftFront Power", drive.leftFront.getPower());
            telemetry.update();

            if (gamepad1.a) {
                // Try a very simple movement
                Actions.runBlocking(drive.actionBuilder(startPose)
                        .lineToX(10)
                        .build());
            }
        }
    }
}