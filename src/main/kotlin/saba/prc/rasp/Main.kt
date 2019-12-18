package saba.prc.rasp

import arduino.Arduino
import com.pi4j.io.gpio.*

fun main() {
	val portForAndroid = 2222
	val portToPC = 3333
	val bufferSize = 16
	val toArduinoSerialPort = "/dev/ttyACM0"//"COM6"
	val udpFromAndroid = UdpConnection(bufferSize, "ffffffff")
	val udpToPC = UdpConnection(bufferSize, "ffffffff")
	var received: String? = null
	var point = 0
	val gpio = GpioFactory.getInstance()
	val pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Pin0", PinPullResistance.PULL_UP)
	val pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "Pin1", PinPullResistance.PULL_UP)
	val arduino = Arduino(toArduinoSerialPort, 9600)
	if (arduino.openConnection()) println("port is opened")
	else println("port was not opened")
	println("Start")
	println("Enter 1 to switch LED on and 0  to switch LED off")
	
	Thread.sleep(2000)
	
	while (received != udpFromAndroid.finishing) {
		Thread.sleep(100)
		received = udpFromAndroid.receive(portForAndroid).text
		received.let {
			println(it)
			println(it.length)
			if (it.contains('S')) udpToPC.send("192.168.100.35", portToPC, "squeal".toByteArray())
			if (it.contains('B')) udpToPC.send("192.168.100.35", portToPC, "bomb!".toByteArray())
			arduino.serialWrite(it)
		}
	}
	arduino.closeConnection()
	System.exit(0)
}