package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class LauncherWheel {

    private final CRServo launcherWheel;
    private static final double POWER = 1.0;
    private boolean toggled = false;
    private boolean lastBPressed = false;

    public LauncherWheel(CRServo launcherWheel) {
        this.launcherWheel = launcherWheel;
    }

    public void init() {
        launcherWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        launcherWheel.setPower(0.0);
    }

    // UPDATED METHOD
    public void update(boolean bPressed, boolean override) {
        // Normal toggle logic updates the 'toggled' variable
        if (bPressed && !lastBPressed) {
            toggled = !toggled;
        }
        lastBPressed = bPressed;

        // Priority: If override (Y button) is held, spin.
        // Otherwise, use the toggle state.
        if (override) {
            launcherWheel.setPower(POWER);
        } else {
            launcherWheel.setPower(toggled ? POWER : 0.0);
        }
    }

    public void stop() {
        toggled = false;
        launcherWheel.setPower(0.0);
    }
}