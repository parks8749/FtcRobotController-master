package org.firstinspires.ftc.teamcode.Core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class FlyWheels {

    private final DcMotor leftFlyWheel;
    private final DcMotor rightFlyWheel;
    private static final double POWER = 0.85; // original: 1.0

    private enum State { STOPPED, FORWARD, REVERSE }
    private State state = State.STOPPED;

    private boolean prevLeftPressed = false;
    private boolean prevRightPressed = false;

    public FlyWheels(DcMotor leftFlyWheel, DcMotor rightFlyWheel) {
        this.leftFlyWheel = leftFlyWheel;
        this.rightFlyWheel = rightFlyWheel;
    }

    public void init() {
        if (leftFlyWheel == null || rightFlyWheel == null) {
            throw new IllegalStateException("Flywheel motors not initialized (null).");
        }
        leftFlyWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFlyWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        rightFlyWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFlyWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFlyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        applyPower(false);
    }

    // UPDATED METHOD
    public void update(boolean leftPressed, boolean rightPressed, boolean override) {
        boolean leftRising = leftPressed && !prevLeftPressed;
        boolean rightRising = rightPressed && !prevRightPressed;

        prevLeftPressed = leftPressed;
        prevRightPressed = rightPressed;

        // Update state based on bumpers
        if (leftRising && rightRising) {
            state = State.STOPPED;
        } else if (leftRising) {
            state = (state == State.FORWARD) ? State.STOPPED : State.FORWARD;
        } else if (rightRising) {
            state = (state == State.REVERSE) ? State.STOPPED : State.REVERSE;
        }

        // Pass the override to the power applicator
        applyPower(override);
    }

    private void applyPower(boolean override) {
        double p;

        // If Override is ON, force Forward power
        if (override) {
            p = POWER;
        } else {
            // Otherwise use the State machine
            switch (state) {
                case FORWARD: p = POWER; break;
                case REVERSE: p = -POWER; break;
                default: p = 0.0; break;
            }
        }

        leftFlyWheel.setPower(p);
        rightFlyWheel.setPower(p);
    }

    public void stop() {
        state = State.STOPPED;
        applyPower(false);
    }
}