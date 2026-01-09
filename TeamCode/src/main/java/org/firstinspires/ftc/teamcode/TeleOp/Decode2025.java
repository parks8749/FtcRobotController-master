package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.Core.*;

@TeleOp(name="Decode2025", group="TeleOp")
public class Decode2025 extends LinearOpMode {
    public DriveTrain driveTrain;
    public BackBottom backBottom;
    public BackIntake backIntake;
    public LauncherWheel launcherWheel;
    public FlyWheels flyWheels;
    public Belts belts;
    public FrontIntake frontIntake;

    private static final float STICK_DEADZONE = 0.08f;

    @Override
    public void runOpMode() {
        driveTrain   = new DriveTrain(hardwareMap, "leftFront", "leftBack", "rightFront", "rightBack");
        backBottom   = new BackBottom(hardwareMap.get(CRServo.class, "BackBottom"));
        backIntake   = new BackIntake(hardwareMap.get(CRServo.class, "BackIntake"));
        launcherWheel = new LauncherWheel(hardwareMap.get(CRServo.class, "LauncherWheel"));
        frontIntake = new FrontIntake(hardwareMap.get(CRServo.class, "FrontIntake"));

        flyWheels = new FlyWheels(
                hardwareMap.get(DcMotor.class, "leftFly"),
                hardwareMap.get(DcMotor.class, "rightFly")
        );
        belts = new Belts(
                hardwareMap.get(CRServo.class, "LeftBelt"),
                hardwareMap.get(CRServo.class, "RightBelt")
        );

        backBottom.init();
        backIntake.init();
        launcherWheel.init();
        flyWheels.init();
        belts.init();
        frontIntake.init();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            driveTrain.Drive(gamepad1);

            float leftStick = applyDeadzone(gamepad2.left_stick_y, STICK_DEADZONE);
            float rightStick = applyDeadzone(gamepad2.right_stick_y, STICK_DEADZONE);

            // --- Capture the Override Button ---
            boolean isAllActive = gamepad2.y;

            // --- Pass gamepad2.y into the subsystems ---
            launcherWheel.update(gamepad2.b, isAllActive);
            backIntake.update(leftStick, isAllActive);
            backBottom.update(belts.getMode(), gamepad2.left_stick_y, isAllActive);
            flyWheels.update(gamepad2.right_bumper, gamepad2.left_bumper, isAllActive);

            // Belts and FrontIntake remain normal (unless you want to add it there too)
            belts.update(rightStick);
            frontIntake.update(belts.getMode());

            telemetry.update();
            sleep(10);
        }
    }

    private float applyDeadzone(float val, float dz) {
        return Math.abs(val) < dz ? 0.0f : val;
    }
}