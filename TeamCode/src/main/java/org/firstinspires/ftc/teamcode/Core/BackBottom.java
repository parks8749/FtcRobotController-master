package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BackBottom {

    private final CRServo backBottom;
    private static final double POWER = 1.0;
    private static final float DEADZONE = 0.08f;

    public BackBottom(CRServo backRoller) {
        this.backBottom = backRoller;
    }

    public void init() {
        backBottom.setDirection(DcMotorSimple.Direction.REVERSE);
        backBottom.setPower(0.0);
    }

    public void update(int beltsMode, float leftStickY, boolean override) {
        if (override) {
            // Adjust sign here if it spins backwards on 'Y' press
            backBottom.setPower(POWER);
            return;
        }

        // 2) left-stick overrides
        if (Math.abs(leftStickY) >= DEADZONE) {
            backBottom.setPower(leftStickY > 0 ? POWER : -POWER);
            return;
        }

        // 3) belts mode
        switch (beltsMode) {
            case 1: backBottom.setPower(-POWER); break;
            case 2: backBottom.setPower(POWER); break;
            default: backBottom.setPower(0.0); break;
        }
    }

    public void stop() {
        backBottom.setPower(0.0);
    }
}