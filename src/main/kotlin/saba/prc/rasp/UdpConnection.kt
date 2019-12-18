package saba.prc.rasp

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.charset.Charset
import kotlinx.coroutines.*
import java.net.Inet4Address
import kotlin.system.exitProcess

fun main() = Unit

class UdpConnection(private val bufferSize: Int, val finishing: String) {
	var text: String = "Not initialized!"
		get() {
			val tmp = field
			field = "def"
			//val revocation = data
			return tmp
		}
		private set
	
	var data: ByteArray = ByteArray(bufferSize)
		get() {
			val tmp = field
			field = ByteArray(bufferSize)
			//val revocation = text
			return tmp
		}
		private set
	
	fun receive(port: Int): UdpConnection {
		DatagramSocket(port).use { socket ->
			var packet: DatagramPacket? = null
			try {
				packet = DatagramPacket(data, data.size)
				socket.receive(packet)
			}
			catch (e: Exception) {
				printException(e)
			}
			text = packet?.let { packet.createString() } ?: "Received text is null"
			return this
		}
	}
	
	fun send(ip: String, port: Int, SendData: ByteArray) {
		GlobalScope.launch {
			DatagramSocket(port).use { socket ->
				val packet: DatagramPacket?
				try {
					val address = Inet4Address.getByName(ip)
					packet = DatagramPacket(SendData, SendData.size, address, port)
					socket.send(packet)
				}
				catch (e: Exception) {
					printException(e)
				}
			}
		}
	}
	
	private fun printException(e: Exception) {
		println(e.message)
		PrintWriter(BufferedWriter(FileWriter(System.getProperty("user.dir") + "\\エラーログ.txt", true)))
			.use { it.println(e.message) }
		exitProcess(-1)
	}
}

fun DatagramPacket.createString() = String(this.data, 0, this.length, Charset.forName("ASCII")) + "\n"