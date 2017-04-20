package br.com.vroc.autodeletefile

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import org.junit.Before
import org.junit.After
import java.nio.file.Files

class AutoDeleteFileTest {
	
	val file = File.createTempFile("file_autodelete_test", ".tmp")
	
	@Test fun shouldDeleteAfter2Seconds() {
		file.deleteAfter(2)
		
		assertTrue(file.exists()) 
		
		Thread.sleep(2100)
		
		assertFalse(file.exists()) 
	}
	
	@Test fun shouldNotBeDeleteAfterCancel() {
		file.deleteAfter(2)
		
		assertTrue(file.exists()) 
		
		Thread.sleep(1000)
		
		assertTrue(file.exists())
		assertTrue(file.cancelDeleteSchedule())
		
		Thread.sleep(1100)
		
		assertTrue(file.exists()) 
	}
	
	@Test fun shouldDeleteAfterReset() {
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
	
	@After fun deleteFile() {
        Files.deleteIfExists(file.toPath())
    }
	
}