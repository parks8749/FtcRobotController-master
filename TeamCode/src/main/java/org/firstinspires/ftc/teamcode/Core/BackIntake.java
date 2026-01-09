package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BackIntake {
    private final CRServo backIntake;
    private static final float DEADZONE = 0.08f;
    private static final double POWER = 1.0;

    public BackIntake(CRServo backIntake) {
        this.backIntake = backIntake;
    }

    public void init() {
        backIntake.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    // UPDATED METHOD
    public void update(float leftStickY, boolean override) {
        // 1. Override Priority
        if (override) {
            // Adjust sign (- or +) here if it spins the wrong way on 'Y' press
            backIntake.setPower(-POWER);
            return;
        }

        // 2. Normal Stick Logic
        if (Math.abs(leftStickY) < DEADZONE) {
            backIntake.setPower(0.0);
            return;
        }
        backIntake.setPower(leftStickY > 0 ? -POWER : POWER);
    }
}