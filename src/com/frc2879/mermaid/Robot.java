/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.frc2879.mermaid;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;

/**
 * 
 * @author floogulinc
 * 
 * Robot Code for FRC Team 2879 Orange Thunder
 * COMPETITION BOT - Mermaid
 */
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SimpleRobot {

    RobotDrive drivetrain = new RobotDrive(1, 2);
    //Gamepad gp = new Gamepad(1);
    GamepadXbox gp = new GamepadXbox(1);

    public static final String name = "Mermaid Bot";
    public static final String version = "v1.00";
    public static final String fullname = name + " " + version;

    // CONFIG VALUES
    int DriveSensitivity = 100;
    int TurnSensitivity = 100;
    boolean SquaredInputs = true;

    //boolean sawyerdrive = false;
    int DriveMode = 0; // 0 is arcade, 1 is tank, 2 is sawyer

    DSOutput dsout;

    AxisCamera acam;

    //Called exactly 1 time when the competition starts.
    protected void robotInit() {
        dsout = new DSOutput();
        System.out.println("Loading " + fullname);
        dsout.clearOutput();
        dsout.say(1, "Loading...");
        Timer.delay(1);
        acam = AxisCamera.getInstance("10.28.79.11");
        dsout.say(1, "Welcome to");
        dsout.say(2, fullname);

    }

    ButtonState pbuttonRB = new ButtonState(false);
    ButtonState pbuttonLB = new ButtonState(false);
    ButtonState pbuttonY = new ButtonState(false);
    ButtonState pbuttonX = new ButtonState(false);
    ButtonState pbuttonDPADL = new ButtonState(false);
    ButtonState pbuttonDPADR = new ButtonState(false);
    ButtonState pbuttonBACK = new ButtonState(false);

    boolean bumpertoggle = false; //true is turn sensitivity, false for drive

    public void saydrivesensitivity(int line) {
        if (bumpertoggle) {
            dsout.say(line, "DSensitivity: " + Integer.toString(DriveSensitivity));
        } else {
            dsout.say(line, "*DSensitivity: " + Integer.toString(DriveSensitivity));
        }

    }

    public void sayturnsensitivity(int line) {
        if (bumpertoggle) {
            dsout.say(line, "*TSensitivity: " + Integer.toString(TurnSensitivity));
        } else {
            dsout.say(line, "TSensitivity: " + Integer.toString(TurnSensitivity));
        }

    }

    public void saysquaredinputs(int line) {
        dsout.say(line, "Squared Inputs: " + SquaredInputs);
    }

    String[] drivemodename = {
        "Arcade",
        "Tank",
        "Sawyer"};

    public void saydrivemode(int line) {
        dsout.say(line, "Mode: " + drivemodename[DriveMode]);
    }

    public void sayscreentele() {

        dsout.say(1, fullname);
        saydrivemode(2);
        saysquaredinputs(3);
        if (DriveMode == 1) {
            dsout.clearLine(4);
            dsout.clearLine(5);
        } else {
            saydrivesensitivity(4);
            sayturnsensitivity(5);
        }
    }

    public void switchdrivemode() {
        switch (DriveMode) {
            case 0:
                DriveMode = 1;
                break;
            case 1:
                DriveMode = 2;
                break;
            case 2:
                DriveMode = 0;
                break;
            default:
                DriveMode = 0;
                break;
        }
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        if (isAutonomous()) {
            dsout.clearOutput();
            dsout.say(1, fullname);
            dsout.say(2, "Autonomous Mode");
            Timer time = new Timer();
            time.start();
            while (time.get() < 1 && isEnabled() && isAutonomous()) {
                drivetrain.drive(-0.3, 0); //Negative goes forward for some reason
                dsout.say(3, "Time: " + time.get());
                Timer.delay(0.01);
            }
            time.stop();
            dsout.say(3, "Time: " + time.get() + " DONE");
            time.reset();
        }
    }

    public boolean buttoncheck(boolean button, ButtonState pbutton) {
        if (button) {
            pbutton.setstate(true);
            return false;
        } else if (pbutton.getstate() && !button) {
            pbutton.setstate(false);
            return true;
        } else {
            pbutton.setstate(false);
            return false;
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        drivetrain.setSafetyEnabled(true);
        dsout.clearOutput();
        sayscreentele();
        while (isOperatorControl() && isEnabled()) {

            // Update joystick values:
            double move = 0;
            double spin = 0;

            double tankleft = 0;
            double tankright = 0;

            if (DriveMode == 0) {
                move = (gp.getLeftY() * ((double) (DriveSensitivity) / 100));
                spin = (gp.getLeftX() * ((double) (TurnSensitivity) / 100));
            } else if (DriveMode == 2) {
                move = (gp.getLeftY() * ((double) (DriveSensitivity) / 100));
                spin = (gp.getRightX() * ((double) (TurnSensitivity) / 100));
            } else if (DriveMode == 1) {
                tankleft = gp.getLeftY();
                tankright = gp.getRightY();
            }

            if (DriveMode != 1) {
                if (buttoncheck(gp.getButtonStateRightBumper(), pbuttonRB)) {
                    if (bumpertoggle) {
                        TurnSensitivity = (TurnSensitivity) + (10);
                        sayscreentele();
                    } else {
                        DriveSensitivity = (DriveSensitivity) + (10);
                        sayscreentele();
                    }
                }

                if (buttoncheck(gp.getButtonStateLeftBumper(), pbuttonLB)) {
                    if (bumpertoggle) {
                        TurnSensitivity = (TurnSensitivity) - (10);
                        sayscreentele();
                    } else {
                        DriveSensitivity = (DriveSensitivity) - (10);
                        sayscreentele();
                    }
                }

                if (buttoncheck(gp.getButtonStateY(), pbuttonY)) {
                    bumpertoggle = !bumpertoggle;
                    sayscreentele();
                }
            }

            if (buttoncheck(gp.getButtonStateX(), pbuttonX)) {
                SquaredInputs = !SquaredInputs;
                sayscreentele();
            }

            if (buttoncheck(gp.getButtonStateBACK(), pbuttonBACK)) {
                switchdrivemode();
                sayscreentele();
            }

            // Drive da robot:
            if (DriveMode == 1) {
                drivetrain.tankDrive(tankleft, tankright, SquaredInputs);
            } else {
                drivetrain.arcadeDrive(move, spin, SquaredInputs);
            }

            Timer.delay(0.005);
        }
        dsout.clearOutput();
    }

    protected void disabled() {
        if (isDisabled()) {
            dsout.clearOutput();
            dsout.say(1, fullname);
            dsout.say(2, "Robot Disabled");
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }
}
