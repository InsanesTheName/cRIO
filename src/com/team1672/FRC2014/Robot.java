/*
    SARCS (Semi-Automatic Robot Control System)
    v0.1 'coolblock'
    
    This code is to be run on the cRIO.

    Copyright �2014 Jeff Meli.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.team1672.FRC2014;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SimpleRobot {

    protected long ticks;
    protected long timeOfFirstTick;
    
    public final int FIRE_BUTTON = 1;
    public final int TOGGLE_DRIVE_MODE_BUTTON = 2;
    
    public final RobotDrive motors;
    public final Joystick joy1, joy2;
    public final DoubleSolenoid pneumatic1;
    protected boolean tankDrive;

    public Robot() 
    {
        motors = new RobotDrive(1, 2, 3, 4); //4 Jaguars connected to PWM ports 1-4
        joy1 = new Joystick(1);
        joy2 = new Joystick(2);
        pneumatic1 = new DoubleSolenoid(1, 2);
     //   pneumatic2 = new Solenoid(2);
        System.out.println("P1 Constructed... " + pneumatic1.get());
    //    System.out.println("P2 Constructed... " + pneumatic2.get());
        pneumatic1.set(DoubleSolenoid.Value.kOff);
    //    pneumatic2.set(false);
        tankDrive = true;
        
        ticks = 1;
        timeOfFirstTick = System.currentTimeMillis();
    }
    
    public void autonomous() 
    {
        System.out.println("Autonomous mode has no purpose currently; switch to manual operation.");
    }

    public void operatorControl() 
    {
        System.out.println("Driver operation enabled. Using tank drive mode.");
        motors.setSafetyEnabled(false);
        long ticksBeforeToggleAgain = 0;
        
        while(this.isOperatorControl() && this.isEnabled())
        {
            if(tankDrive)
            {
                motors.tankDrive(joy1, joy2);
            }
            else
            {
                motors.arcadeDrive(joy1);
            }
            
            if(ticksBeforeToggleAgain > 0)
            {
                ticksBeforeToggleAgain--;
            }
            
            if(joy1.getRawButton(TOGGLE_DRIVE_MODE_BUTTON) && ticksBeforeToggleAgain == 0)
            {
                tankDrive = !tankDrive;
                ticksBeforeToggleAgain = 100;
            }
            
            if(joy1.getRawButton(FIRE_BUTTON))
            {
                pneumatic1.set(DoubleSolenoid.Value.kForward);
            }
            else if(joy2.getRawButton(FIRE_BUTTON))
            {
                pneumatic1.set(DoubleSolenoid.Value.kOff);
            }
            else
            {
                pneumatic1.set(DoubleSolenoid.Value.kReverse);
            }
            System.out.println(pneumatic1.get());
            
            if(ticks % 100 == 0)
                System.out.println("Current tick: " + ticks);
            ticks++;
        }
        
    }

    public void test() 
    {
        System.out.println("Test mode enabled. \n");
        System.out.println(motors.getDescription() + ", " + motors.toString());
        System.out.println(joy1.toString());
        System.out.println(joy2.toString());
        System.out.println(pneumatic1.toString());
    }
}