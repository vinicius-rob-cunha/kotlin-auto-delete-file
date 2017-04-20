package br.com.vroc.autodeletefile

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class AutoDeleteFileTest {
	
	@Test fun shouldDeleteAfter2Seconds() {
		val file = File.createTempFile("test", "tmp")
		file.deleteAfter(2)
		
		assertTrue(file.exists()) 
		
		Thread.sleep(2100)
		
		assertFalse(file.exists()) 
	}
	
	@Test fun shouldNotBeDeleteAfterCancel() {
		val file = File.createTempFile("test", "tmp")
		file.deleteAfter(2)
		
		assertTrue(file.exists()) 
		
		Thread.sleep(1000)
		
		assertTrue(file.exists())
		assertTrue(file.cancelDeleteSchedule())
		
		Thread.sleep(1100)
		
		assertTrue(file.exists()) 
	}
	
	@Test fun shouldDeleteAfterReset() {
		val file = File.createTempFile("test", "tmp")
		file.deleteAfter(2)
		
		assertTrue(file.exists()) 
		
		Thread.sleep(1000)
		
		assertTrue(file.exists())
		assertTrue(file.resetDeleteSchedule())
		
		Thread.sleep(1100)
		
		//passed 2.1 seconds
		assertTrue(file.exists())
		
		Thread.sleep(1000)
		
		//passed 3.1 seconds (1 of first sleep, plus 2 of reset)
		assertFalse(file.exists()) 
	}
	
}