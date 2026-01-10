package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BackIntake {
    private final CRServo backIntake;
    private static final float DEADZONE = 0.08f;
    private static final double POWER = 1.0;
    private static final double BELTS_SLOW_POWER = 0.1; // slow speed when belts active

    public BackIntake(CRServo backIntake) {
        this.backIntake = backIntake;
    }

    public void init() {
        backIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        backIntake.setPower(0.0);
    }

    /**
     * Priority:
     * 1) aPressed -> full power while held (immediate)
     * 2) override -> full power (next)
     * 3) beltsMode != 0 -> slow belts behavior (optional)
     * 4) stick -> full POWER
     */
    public void update(float leftStickY, boolean override, int beltsMode, boolean aPressed) {

        // 1) A button hold should immediately run the intake and return
        if (aPressed) {
            backIntake.setPower(-POWER); // kept same sign as before
            return;
        }

        // 2) Override (Y) has next priority
        if (override) {
            backIntake.setPower(-POWER);
            return;
        }

        // (Optional) belts slow behavior if you want belts to drive the back intake slowly:
        /*
        if (beltsMode == 1) {
            backIntake.setPower(-BELTS_SLOW_POWER);
            return;
        } else if (beltsMode == 2) {
            backIntake.setPower(BELTS_SLOW_POWER);
            return;
        }
        */

        // 3) Stick control
        if (Math.abs(leftStickY) < DEADZONE) {
            backIntake.setPower(0.0);
            return;
        }
        backIntake.setPower(leftStickY > 0 ? -POWER : POWER);
    }

    public void stop() {
        backIntake.setPower(0.0);
    }
}
