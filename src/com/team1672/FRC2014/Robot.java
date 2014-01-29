/*
    SARCS (Semi-Automatic Robot Control System)
    v0.3 'teleburger'
    
    This code is to be run on the cRIO.

    Copyright �2014 Jeff Meli, Tommy Bohde.

    SARCS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SARCS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SARCS. If not, see <http://www.gnu.org/licenses/>.
*/
package com.team1672.FRC2014;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SimpleRobot {

    protected long ticks;
    
    public final long START_TIME;
    
    public final int FIRE_BUTTON = 1;
    public final int TOGGLE_DRIVE_MODE_BUTTON = 2;
    public final int COMPRESSOR_BUTTON = 3;
    
    public final int LEFT_JOYSTICK_CHANNEL = 1;
    public final int RIGHT_JOYSTICK_CHANNEL = 2;
    
    public final int COMPRESSOR_RELAY_PORT = 1;
    public final int COMPRESSOR_SWITCH_PORT = 1;
    
    public final int ULTRASONIC_CHANNEL = 1;
    
    public final RobotDrive motors;
    public final Joystick leftStick, rightStick;
    public final DoubleSolenoid pneumatic1, pneumatic2;
    public final Compressor compressor;
    public final AnalogChannel ultrasonic1;
    protected boolean tankDrive;

    public Robot() 
    {
        motors = new RobotDrive(1, 2, 3, 4); //4 Jaguars connected to PWM ports 1-4
        
        leftStick = new Joystick(LEFT_JOYSTICK_CHANNEL);
        rightStick = new Joystick(RIGHT_JOYSTICK_CHANNEL);
        
        pneumatic1 = new DoubleSolenoid(1, 2);
        pneumatic1.set(DoubleSolenoid.Value.kOff);
        
        pneumatic2 = new DoubleSolenoid(3, 4);
        pneumatic2.set(DoubleSolenoid.Value.kOff);
        
        compressor = new Compressor(COMPRESSOR_SWITCH_PORT, COMPRESSOR_RELAY_PORT);
        compressor.stop();
        
        ultrasonic1 = new AnalogChannel(1);
        
        tankDrive = true;
        
        ticks = 1;
        START_TIME = System.currentTimeMillis();
    }
    
    public void autonomous() 
    {
        System.out.println("Autonomous mode has no purpose currently; switch to manual operation.");
    }

    public void operatorControl() 
    {
        System.out.println("Driver operation enabled. Using tank drive mode.");
        motors.setSafetyEnabled(false);
        long lastToggle = 0;
        
        while(this.isOperatorControl() && this.isEnabled())
        {
            System.out.println("Analog channel 1: " + ultrasonic1.getValue());
            
            if(tankDrive)
            {
                motors.tankDrive(leftStick, rightStick);
            }
            else
            {
                motors.arcadeDrive(leftStick);
            }
            
            if(!compressor.getPressureSwitchValue())
            {
                compressor.start();
            }
            else
            {
                compressor.stop();
            }

            if(rightStick.getRawButton(TOGGLE_DRIVE_MODE_BUTTON) && System.currentTimeMillis() - lastToggle > 500L)
            {
                tankDrive = !tankDrive;
                lastToggle = System.currentTimeMillis();
            }
            
            //TOUCH ME AND I KILL YOU
            if(rightStick.getRawButton(FIRE_BUTTON))
            {
                pneumatic1.set(DoubleSolenoid.Value.kForward);
                long buttonPressed = System.currentTimeMillis();
                pneumatic2.set(DoubleSolenoid.Value.kForward);
                System.out.println("Time between piston fires: " + (System.currentTimeMillis() - buttonPressed));
            }
            else if(leftStick.getRawButton(FIRE_BUTTON))
            {
                pneumatic1.set(DoubleSolenoid.Value.kReverse);
                pneumatic2.set(DoubleSolenoid.Value.kReverse);
            }
            else
            {
                pneumatic1.set(DoubleSolenoid.Value.kOff);
                pneumatic2.set(DoubleSolenoid.Value.kOff);
            }
            if(ticks % 1000 == 0)
            {
                System.out.println("Current tick: " + ticks);
            }
            ticks++;
        }
    }
      
    public void test() 
    {
        System.out.println("Test mode enabled. \n");
        System.out.println(motors.getDescription() + ", " + motors.toString());
        System.out.println(leftStick.toString());
        System.out.println(rightStick.toString());
        System.out.println(pneumatic1.toString());
    }
      
}
