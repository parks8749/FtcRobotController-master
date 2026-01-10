package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class LauncherWheel {

    private final CRServo launcherWheel;
    private static final double POWER = 1.0;
    private boolean toggledB = false;
    private boolean lastBPressed = false;

    public LauncherWheel(CRServo launcherWheel) {
        this.launcherWheel = launcherWheel;
    }

    public void init() {
        launcherWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        launcherWheel.setPower(0.0);
    }

    // UPDATED METHOD
    public void update(boolean bPressed, boolean override, boolean aPressed) {

        // 1) Maintain toggle state for B (edge detection)
        if (bPressed && !lastBPressed) {
            toggledB = !toggledB;
        }
        lastBPressed = bPressed;

        // 2) A hold should immediately run the launcher while held (highest priority)
        if (aPressed) {
            launcherWheel.setPower(POWER);
            return;
        }

        // 3) Override (Y) next priority
        if (override) {
            launcherWheel.setPower(POWER);
            return;
        }

        // 4) Otherwise follow toggledB state
        launcherWheel.setPower(toggledB ? POWER : 0.0);
    }

    public void stop() {
        toggledB = false;
        launcherWheel.setPower(0.0);
    }
}
