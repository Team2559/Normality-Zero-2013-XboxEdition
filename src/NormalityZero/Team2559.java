/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package NormalityZero;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Team2559 extends IterativeRobot {

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */

    // Joystick
    Joystick xbox = new Joystick(1); // Drive Stick (XBOX)
    String driveMethod = "tank";

    // Motors
    Jaguar jaguar1 = new Jaguar(1); // Left Front Drive
    Jaguar jaguar2 = new Jaguar(2); // Right Front Drive
    Jaguar jaguar5 = new Jaguar(5); // Left Back Drive
    Jaguar jaguar6 = new Jaguar(6); // Right Back Drive
    Jaguar jaguar3 = new Jaguar(3); // Front Shoot Wheel
    Jaguar jaguar4 = new Jaguar(4); // Back Shoot Wheel

    // Relay
    Relay spike1 = new Relay(2); // Cannon Relay

    // Solenoid
    Solenoid solenoid1 = new Solenoid(1); // Super Shifter Default (False)
    Solenoid solenoid2 = new Solenoid(2); // Super Shifter Default (True)
    Solenoid solenoid3 = new Solenoid(3); // Frisbee Launcher (True)
    Solenoid solenoid4 = new Solenoid(4); // Frisbee Launcher (False)
    Solenoid solenoid5 = new Solenoid(5); // Pinball Kick (False)
    Solenoid solenoid6 = new Solenoid(6); // Pinball Kick (True)
    Solenoid solenoid7 = new Solenoid(7); // Shooter Lift (False)
    Solenoid solenoid8 = new Solenoid(8); // Shooter Lift (True)

    // Compressor
    Compressor Compressor = new Compressor(1, 1);

    // Drive
    RobotDrive robotDrive = new RobotDrive(jaguar1, jaguar5, jaguar2, jaguar6);

    // Digital Input
    DigitalInput lightsensor = new DigitalInput(2);

    // Timer
    Timer systimer = new Timer();

    // Boolean
    boolean belt_button_pressed = false;
    boolean belts_stopped = true;
    boolean prevIterButton1Closed = false;
    boolean prevIterButton4Closed = false;
    boolean prevIterButton5Closed = true;
    boolean prevIterButton3Joy2Closed = false;
    boolean shifterToggle = false;
    boolean shooterLift = false;
    boolean prevIterCannonButton3Closed = false;

    // Int
    int maxShootCount;
    int currShootCount;

    // Camera
    AxisCamera camera; // Axis camera object (connected to the switch)

    public void robotInit() {
        Compressor.start();
        camera = AxisCamera.getInstance(); // Get an instance of the camera
    }

    public void autonomousInit() {
        solenoid1.set(false); // Enable Pneumatic Shifter
        solenoid2.set(true); // Enable Pneumatic Shifter

        jaguar3.set(0.56); // Front Shoot Wheel
        jaguar4.set(0.56); // Back Shoot Wheel

        Timer.delay(4); // Delay next string of code for 4 Seconds

        maxShootCount = 2; // Only Does 3 Shots Most
        currShootCount = 0; // Starts At 0 Shots
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        if (currShootCount < maxShootCount) // If our current shot count is less than the max(2 shot max) then do the below code
        {
            solenoid3.set(true); // Activate Frisbee Launcher
            solenoid4.set(false); // Activate Frisbee Launcher
            Timer.delay(0.5); // Delay Next String Of Code For 0.5 Seconds
            solenoid3.set(false); // Deactivate Frisbee Launcher
            solenoid4.set(true); // Deactivate Frisbee Launcher
            solenoid5.set(true); // Activate Frisbee Dropper
            solenoid6.set(false); // Activate Frisbee Dropper
            Timer.delay(0.4); // Delay Next String Of Code For 0.4 Seconds
            solenoid5.set(false); // Deactivate Frisbee Dropper
            solenoid6.set(true); // Deactivate Frisbee Dropper
            Timer.delay(3.0); // Delay Next String Of Code For 2.0 Seconds
        }
        currShootCount++; // Add on a count when code cycles through each time until it hits 2 shots
    }

    public void teleopInit() {
        spike1.set(Relay.Value.kOff);
    }

    /**
     * This function is called periodically during operator control
     */

    public void teleopPeriodic() {
        Watchdog.getInstance().feed(); // Nibbles N Bits

        if (xbox.getRawButton(3) && !prevIterButton1Closed) {
            spike1.set(Relay.Value.kForward);
            Timer.delay(0.25);
            spike1.set(Relay.Value.kOff);
        }
        prevIterButton1Closed = xbox.getRawButton(3);

        if (xbox.getRawButton(5)) { // Brake
            robotDrive.drive(0, 0);
        } else {
            if (driveMethod.equals("tank")) {
                robotDrive.tankDrive(xbox.getRawAxis(2), xbox.getRawAxis(5));
            } else if (driveMethod.equals("arcade")) {
                robotDrive.arcadeDrive(xbox);
            }
        }

        if (xbox.getRawButton(6)) { // shift gear, hold down
            solenoid1.set(false); // low gear
            solenoid2.set(true);
        } else {
            solenoid1.set(true); // high gear
            solenoid2.set(false);
        }

        solenoid7.set(xbox.getRawAxis(3) < -0.7);

        solenoid8.set(xbox.getRawAxis(3) > 0.7);

        if (xbox.getRawButton(2)) { // Frisbee Launcher
            solenoid3.set(true); // Frisbee Launcher Activate
            solenoid4.set(false); // Frisbee Launcher Activate
        } else {
            solenoid3.set(false); // Frisbee Launcher Deavtivate
            solenoid4.set(true); // Frisbee Launcher Deavtivate
        }

        if (xbox.getRawButton(1) && !prevIterButton3Joy2Closed) // Reload Frisbee *DO NOT HOLD
        {
            solenoid5.set(true); // Activate Dropper
            solenoid6.set(false); // Activate Dropper
            Timer.delay(0.4); // Delay Time For Next String Of Code
            solenoid5.set(false); // Deactivate Dropper
            solenoid6.set(true); // Deavtivate Dropper
            Timer.delay(0.5);
        }
        prevIterButton3Joy2Closed = xbox.getRawButton(1);

        if (xbox.getRawButton(4)) { // Rapid fire
            solenoid3.set(true);
            solenoid4.set(false);
            Timer.delay(0.2);
            solenoid3.set(false);
            solenoid4.set(true);
            solenoid5.set(true);
            solenoid6.set(false);
            Timer.delay(0.3);
            solenoid5.set(false);
            solenoid6.set(true);
            Timer.delay(0.3);
        }

        if (xbox.getRawButton(7)) { // Warm up normal shoot
            jaguar3.set(0.58); // Top Motor Is At 64%
            jaguar4.set(0.58); // Bottom Motor Is At 64%
        } else if (xbox.getRawButton(8)) { // Full speed warm up
            jaguar3.set(1);
            jaguar4.set(1);
        } else { // Motors Off
            jaguar3.set(0); // Top Motor Is At 0%
            jaguar4.set(0); // Bottom Motor Is At 0%
        }

        if (xbox.getRawButton(10) && xbox.getRawButton(9)) {
            if (driveMethod.equals("arcade")) {
                driveMethod = "tank";
            } else if (driveMethod.equals("tank")) {
                driveMethod = "arcade";
            }
        }
    }
}
