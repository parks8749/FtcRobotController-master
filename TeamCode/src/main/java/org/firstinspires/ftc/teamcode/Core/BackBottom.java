package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BackBottom {

    private final CRServo backBottom;

    private static final double POWER = 0.5;

    private static final double FRONT_INTAKE_POWER = 0.5;

    private static final float DEADZONE = 0.08f;

    public BackBottom(CRServo backRoller) {
        this.backBottom = backRoller;
    }

    public void init() {
        backBottom.setDirection(DcMotorSimple.Direction.REVERSE);
        backBottom.setPower(0.0);
    }

    public void update(int beltsMode,
                       float leftStickY,
                       boolean override,
                       boolean frontIntakeActive) {

        if (frontIntakeActive) {
            // Match belts direction but very slowly
            if (beltsMode == 1) {
                backBottom.setPower(-FRONT_INTAKE_POWER);
            } else if (beltsMode == 2) {
                backBottom.setPower(FRONT_INTAKE_POWER);
            } else {
                backBottom.setPower(0.0);
            }
            return;
        }

        // Override (Y button)
        if (override) {
            backBottom.setPower(-POWER);
            return;
        }

        // Stick control
        if (Math.abs(leftStickY) >= DEADZONE) {
            backBottom.setPower(leftStickY > 0 ? POWER : -POWER);
            return;
        }

        // Belts fallback
        switch (beltsMode) {
            case 1:
                backBottom.setPower(-POWER);
                break;
            case 2:
                backBottom.setPower(POWER);
                break;
            default:
                backBottom.setPower(0.0);
                break;
        }
    }

    public void stop() {
        backBottom.setPower(0.0);
    }
}
