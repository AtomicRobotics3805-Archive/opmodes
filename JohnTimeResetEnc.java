/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TeleOp Mode
 * <p>
 *Enables control of the robot via the gamepad
 */

public class JohnTimeResetEnc extends OpMode {


  DcMotor AMotor;
  private String startDate;
  private ElapsedTime runtime = new ElapsedTime();
  double lastTime=0;
  double deltaTime=0;
  double maxTime = 0;
  double minTime = 10000;
  double avgTime = 0;
  double avgTimeTot = 0;
  double cntr = 0;
  double minCntr = 0;
  double maxCntr = 0;
  int state =0;
  int Fail = 0;
  @Override
  public void init() {


  AMotor = hardwareMap.dcMotor.get("motor_2");
  }

  /*
       * Code to run when the op mode is first enabled goes here
       * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
       */
  @Override
  public void init_loop() {
    startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    runtime.reset();
    telemetry.addData("Null Op Init Loop", runtime.toString());
  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */
  @Override
  public void loop() {

    switch(state) {
      case 0:
        AMotor.setMode((DcMotorController.RunMode.RUN_WITHOUT_ENCODERS));
        AMotor.setPower(1);
        state =1;
      case 1:
        lastTime =runtime.time();
        AMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        state = 2;
        break;
      case 2:
        deltaTime = runtime.time() - lastTime;
        if (deltaTime < minTime) {
          minTime = deltaTime;
          minCntr = cntr;
        }
        if (deltaTime > maxTime) {
          maxTime = deltaTime;
          maxCntr = cntr;
        }
        cntr += 1;
        avgTimeTot += deltaTime;
        avgTime =avgTimeTot/cntr;
        state = 0;
        if (AMotor.getCurrentPosition() != 0) Fail +=1;
        break;
    }

    telemetry.addData("1 Start", "NullOp started at " + startDate);
    telemetry.addData("2 Status", "running for " + runtime.toString());
    telemetry.addData("3", "Max " + maxTime*1000);
    telemetry.addData("4", "Min " + minTime*1000);
    telemetry.addData("5", "Avg " + avgTime*1000);
  telemetry.addData("6", "Min Cntr " + minCntr);
    telemetry.addData("7", "Max Cntr " + maxCntr);
    telemetry.addData("8", "Fail " + Fail);
  }
}
