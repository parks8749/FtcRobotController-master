package org.firstinspires.ftc.teamcode.Autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// We need to import MecanumDrive and PinpointLocalizer because
// they are now in a different folder (the parent folder)
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;

@Autonomous(name = "MyAuto", group = "Autonomous")

public class MyAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        // 1. Initialize the start position
        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        // 2. Reset Pinpoint (Crucial for the computer to know where (0,0) is)
        if (drive.localizer instanceof PinpointLocalizer) {
            PinpointLocalizer pinpoint = (PinpointLocalizer) drive.localizer;
            pinpoint.driver.resetPosAndIMU();
            sleep(300); // Give it a moment to calibrate
        }

        // 3. Build the Trajectory
        Action trajectoryAction = drive.actionBuilder(startPose)
                .lineToY(24)
                .turn(Math.toRadians(90))
                .lineToX(24)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        // 4. Execute
        Actions.runBlocking(trajectoryAction);
    }
}