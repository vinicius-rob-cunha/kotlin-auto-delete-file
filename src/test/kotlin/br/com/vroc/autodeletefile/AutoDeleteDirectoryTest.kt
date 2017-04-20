package br.com.vroc.autodeletefile

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import org.junit.Before
import org.junit.After
import java.nio.file.Files

class AutoDeleteDirectoryTest {
	
	val directory = Files.createTempDirectory("kotlinautodelete").toFile()
	
	@Before fun fillFolder() {
		
		for(i in 1..10) {
			File.createTempFile("del", ".tmp", directory)
		}
		
        val fooDir = Files.createTempDirectory(directory.toPath(), "foo").toFile()
		File.createTempFile("bar", ".tmp", fooDir)
		File.createTempFile("bar", ".tmp", fooDir)
    }
	
	@Test fun shouldDeleteAfter2Seconds() {
		directory.deleteAfter(2)
		
		assertTrue(directory.exists()) 
		
		Thread.sleep(2100)
		
		assertFalse(directory.exists()) 
	}
	
	@Test fun shouldNotBeDeleteAfterCancel() {
		directory.deleteAfter(2)
		
		assertTrue(directory.exists()) 
		
		Thread.sleep(1000)
		
		assertTrue(directory.exists())
		assertTrue(directory.cancelDeleteSchedule())
		
		Thread.sleep(1100)
		
		assertTrue(directory.exists()) 
	}
	
	@Test fun shouldDeleteAfterReset() {
		directory.deleteAfter(2)
		
		assertTrue(directory.exists()) 
		
		Thread.sleep(1000)
		
		assertTrue(directory.exists())
		assertTrue(directory.resetDeleteSchedule())
		
		Thread.sleep(1100)
		
		//passed 2.1 seconds
		assertTrue(directory.exists())
		
		Thread.sleep(1000)
		
		//passed 3.1 seconds (1 of first sleep, plus 2 of reset)
		assertFalse(directory.exists()) 
	}
	
	@Test fun cleanFolder() {
		assertFalse(directory.listFiles().isEmpty())
		
		directory.deleteDirContent()
		
		assertTrue(directory.listFiles().isEmpty())
	}
	
	@After fun deleteFile() {
        if(directory.exists()) {
			directory.deleteDirContent()
			directory.delete()
		}
    }
	
}