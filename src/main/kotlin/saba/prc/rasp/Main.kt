package saba.prc.rasp

import arduino.Arduino
import com.pi4j.io.gpio.*
import kotlin.system.exitProcess

fun main() {
	val portForController = 2222
	val portToMusic = 3333
	val bufferSize = 16
	val toArduinoSerialPort = "/dev/ttyACM0"//"COM6"
	val udpFromController = UdpConnection(bufferSize, "L:0,R:0,E:dc\n")
	val udpToMusic = UdpConnection(bufferSize, "L:0,R:0,E:dc\n")
	val musicServerIP = "192.168.100.27"
	var received: String? = null
	var point = 0
	val gpio = GpioFactory.getInstance()
	val pin0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Pin0", PinPullResistance.PULL_DOWN)
	val pin1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "Pin1", PinPullResistance.PULL_DOWN)
	val arduino = Arduino(toArduinoSerialPort, 9600)
	if (arduino.openConnection()) println("port is opened")
	else println("port was not opened")
	println("Start")
	println("Enter 1 to switch LED on and 0  to switch LED off")
	var serialStart = false
	
	Thread.sleep(2000)
//	while (true) {
//		print("pin0: " + pin0.state + ", ")
//		println("pin1: " + pin1.state)
//	}
	println("waiting for pin0...")
	while (pin0.isHigh);
	println("pin0 is LoW")
	while (pin0.isLow) Thread.sleep(50)
	
	// serialStart = true
	println("on A point")
	udpToMusic.send(musicServerIP, portToMusic, "wh".toByteArray())
	while (received != udpFromController.finishing) {
		// Thread.sleep(100)
//		if (pin0.isHigh && !serialStart) {
//			serialStart = true
//			point++
//			println("on A point")
//			udpToMusic.send("localhost", portToMusic, "wh".toByteArray())
//		}
		received = udpFromController.receive(portForController).text
		received.let {
			println(it)
			println(it.length)
			arduino.serialWrite(it)
		}
	}
	println("break while")
	while (pin1.isLow); // B地点検知までするまで捕まえる
	println("pin1 is Low")
	udpToMusic.send(musicServerIP, portToMusic, "st".toByteArray())
	println("finish")
	arduino.closeConnection()
	exitProcess(0)
}