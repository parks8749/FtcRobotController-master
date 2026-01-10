//package org.firstinspires.ftc.teamcode.Core;
//
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//
//public class FlyWheels {
//
//    private final DcMotor leftFly;
//    private final DcMotor rightFly;
//
//    // default (current) operating power
//    private static final double DEFAULT_POWER = 0.85;
//    // full power when X is pressed
//    private static final double FULL_POWER = 1.0;
//
//    public FlyWheels(DcMotor leftFly, DcMotor rightFly) {
//        this.leftFly = leftFly;
//        this.rightFly = rightFly;
//    }
//
//    public void init() {
//        // set directions so same positive power spins the wheels in the shooting direction
//        leftFly.setDirection(DcMotorSimple.Direction.FORWARD);
//        rightFly.setDirection(DcMotorSimple.Direction.REVERSE);
//        leftFly.setPower(0.0);
//        rightFly.setPower(0.0);
//    }
//
//    /**
//     * @param rightBumper  => spin forward at default power while held
//     * @param leftBumper   => spin reverse at default power while held
//     * @param xPressed     => override and run BOTH flywheels at FULL_POWER (1.0) while held
//     * @param overrideY    => (existing Y override) spins them at DEFAULT_POWER while held
//     */
//    public void update(boolean rightBumper, boolean leftBumper, boolean xPressed, boolean overrideY) {
//
//        // Highest-priority: X forces FULL power
//        if (xPressed) {
//            leftFly.setPower(-FULL_POWER);
//            rightFly.setPower(-FULL_POWER);
//            return;
//        }
//
//        // Next priority: global Y override (keeps previous behavior but at DEFAULT_POWER)
//        if (overrideY) {
//            leftFly.setPower(-DEFAULT_POWER);
//            rightFly.setPower(-DEFAULT_POWER);
//            return;
//        }
//
//        // Normal bumper-driven control (existing-style)
//        if (rightBumper) {
//            leftFly.setPower(-DEFAULT_POWER);
//            rightFly.setPower(-DEFAULT_POWER);
//        } else if (leftBumper) {
//            leftFly.setPower(DEFAULT_POWER);
//            rightFly.setPower(DEFAULT_POWER);
//        } else {
//            leftFly.setPower(0.0);
//            rightFly.setPower(0.0);
//        }
//    }
//
//    public void stop() {
//        leftFly.setPower(0.0);
//        rightFly.setPower(0.0);
//    }
//}





package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FlyWheels {

    private final DcMotor leftFly;
    private final DcMotor rightFly;

    // optional enhanced interface (may be null on some hardware)
    private final DcMotorEx leftFlyEx;
    private final DcMotorEx rightFlyEx;

    // default (current) operating power
    private static final double DEFAULT_POWER = 0.85;
    // full power when X is pressed
    private static final double FULL_POWER = 1.0;

    // fallback encoder-delta tracking (used if DcMotorEx velocity is not available)
    private int lastLeftPos = 0;
    private int lastRightPos = 0;
    private long lastTimeMs = 0;
    private final ElapsedTime clock = new ElapsedTime();

    public FlyWheels(DcMotor leftFly, DcMotor rightFly) {
        this.leftFly  = leftFly;
        this.rightFly = rightFly;

        // try to get the enhanced interface; may be null on some controllers
        this.leftFlyEx  = (leftFly  instanceof DcMotorEx) ? (DcMotorEx) leftFly  : null;
        this.rightFlyEx = (rightFly instanceof DcMotorEx) ? (DcMotorEx) rightFly : null;
    }

    public void init() {
        // set directions so same positive power spins the wheels in the shooting direction
        leftFly.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFly.setDirection(DcMotorSimple.Direction.REVERSE);

        // use RUN_USING_ENCODER so the hub returns velocity measurements
        leftFly.setMode(RunMode.RUN_USING_ENCODER);
        rightFly.setMode(RunMode.RUN_USING_ENCODER);

        leftFly.setPower(0.0);
        rightFly.setPower(0.0);

        // initialize fallback tracking
        lastLeftPos = leftFly.getCurrentPosition();
        lastRightPos = rightFly.getCurrentPosition();
        lastTimeMs = System.currentTimeMillis();
        clock.reset();
    }

    /**
     * @param rightBumper  => spin forward at default power while held
     * @param leftBumper   => spin reverse at default power while held
     * @param xPressed     => override and run BOTH flywheels at FULL_POWER (1.0) while held
     * @param overrideY    => (existing Y override) spins them at DEFAULT_POWER while held
     */
    public void update(boolean rightBumper, boolean leftBumper, boolean xPressed, boolean overrideY) {

        // Highest-priority: X forces FULL power
        if (xPressed) {
            leftFly.setPower(-FULL_POWER);
            rightFly.setPower(-FULL_POWER);
            return;
        }

        // Next priority: global Y override (keeps previous behavior but at DEFAULT_POWER)
        if (overrideY) {
            leftFly.setPower(-DEFAULT_POWER);
            rightFly.setPower(-DEFAULT_POWER);
            return;
        }

        // Normal bumper-driven control (existing-style)
        if (rightBumper) {
            leftFly.setPower(-DEFAULT_POWER);
            rightFly.setPower(-DEFAULT_POWER);
        } else if (leftBumper) {
            leftFly.setPower(DEFAULT_POWER);
            rightFly.setPower(DEFAULT_POWER);
        } else {
            leftFly.setPower(0.0);
            rightFly.setPower(0.0);
        }
    }

    public void stop() {
        leftFly.setPower(0.0);
        rightFly.setPower(0.0);
    }

    /**
     * Publish flywheel telemetry to the driver station.
     * Call this from your OpMode loop before telemetry.update().
     */
    public void publishTelemetry(Telemetry telemetry) {
        // prefer DcMotorEx.getVelocity() if available (returns ticks/sec), else fallback
        double leftTicksPerSec;
        double rightTicksPerSec;

        if (leftFlyEx != null && rightFlyEx != null) {
            // DcMotorEx.getVelocity() returns ticks per second by default
            leftTicksPerSec  = leftFlyEx.getVelocity();
            rightTicksPerSec = rightFlyEx.getVelocity();
        } else {
            // fallback: compute ticks/sec by delta on current position (less smooth, but works)
            long nowMs = System.currentTimeMillis();
            int curLeft = leftFly.getCurrentPosition();
            int curRight = rightFly.getCurrentPosition();

            long dtMs = Math.max(1, nowMs - lastTimeMs); // avoid div0
            double dtSec = dtMs / 1000.0;

            leftTicksPerSec  = (curLeft - lastLeftPos) / dtSec;
            rightTicksPerSec = (curRight - lastRightPos) / dtSec;

            lastLeftPos = curLeft;
            lastRightPos = curRight;
            lastTimeMs = nowMs;
        }

        // get ticks-per-rev from the configured motor type (works even if unspecified)
        double leftTicksPerRev  = leftFly.getMotorType().getTicksPerRev();
        double rightTicksPerRev = rightFly.getMotorType().getTicksPerRev();

        // avoid divide-by-zero if something is misconfigured
        if (leftTicksPerRev <= 0.0) leftTicksPerRev = 1.0;
        if (rightTicksPerRev <= 0.0) rightTicksPerRev = 1.0;

        // convert to RPM
        double leftRPM  = (leftTicksPerSec  / leftTicksPerRev)  * 60.0;
        double rightRPM = (rightTicksPerSec / rightTicksPerRev) * 60.0;

        telemetry.addData("FlyLeft (RPM)", String.format("%.1f", leftRPM));
        telemetry.addData("FlyLeft (ticks/s)", String.format("%.0f", leftTicksPerSec));
        telemetry.addData("FlyRight(RPM)", String.format("%.1f", rightRPM));
        telemetry.addData("FlyRight(ticks/s)", String.format("%.0f", rightTicksPerSec));
    }
}
