/**
 * Extention for java.util.File class adding methods to delete after few seconds
 * @author Vinicius Cunha
 */

package br.com.vroc.autodeletefile

import br.com.vroc.autodeletefile.util.WeakIdentityHashMap
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

/**
 * Provides property delegation which behaves as if each [R] instance had a backing field of type [T] for that property.
 * Delegation can be defined at top level or inside a class, which will mean that the delegation is scoped to
 * instances of the class -- separate instances will see separate values of the delegated property.
 *
 * This implementation is not thread-safe. Use [SynchronizedFieldProperty] for thread-safe delegation.
 *
 * This delegate does not allow `null` values, use [NullableFieldProperty] for a nullable equivalent.
 *
 * If the delegated property of an [R] instance is accessed but has not been initialized, [initializer] is called to
 * provide the initial value. The default [initializer] throws [IllegalStateException].
 *
 * <p>Based on  https://github.com/h0tk3y/kotlin-fun/blob/master/src/main/kotlin/com/github/h0tk3y/kotlinFun/FieldProperty.kt</p>
 */
class FieldProperty<R, T : Any?>(
    val initializer: (R) -> T = { throw IllegalStateException("Not initialized.") }
) {    
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }
}

/**
 * Singleton to create ScheduledExecutorService
 */
object FileDeleteService {
	val deleteService = Executors.newSingleThreadScheduledExecutor(object : ThreadFactory {
		override fun newThread(r: Runnable?): Thread? {
			var t = Thread(r)
			t.isDaemon = true
			return t;			
		}
	})
}

/**
 * Var to enable new files methods work with ScheduledExecutorService
 */
val File.deleteService: ScheduledExecutorService
	get() = FileDeleteService.deleteService

/**
 * Var to set current scheculed of file
 */
var File.deleteSchedule: ScheduledFuture<*>? by FieldProperty{ null }

/**
 * Var to set the time to delete file. Use to resetDeletionTime method
 */
var File.scheduleTime: Long? by FieldProperty{ null }

/**
 * Delete file after n seconds
 * @param time to delete file in seconds
 */
fun File.deleteAfter(seconds: Long) {
	scheduleTime = seconds
	deleteSchedule = deleteService.schedule({
		deleteDirContent()				
		Files.deleteIfExists(toPath())
	}, seconds, TimeUnit.SECONDS)
}

/**
 * Cancel the schedule to deletion
 * @return true if canceled with success
 */
fun File.cancelDeleteSchedule(): Boolean =
	deleteSchedule?.cancel(false)?:false

/**
 * Cancel the current schedule and create a new one with last time
 * @return true if canceled with success
 */
fun File.resetDeleteSchedule(): Boolean =
	if(deleteSchedule != null) {
		cancelDeleteSchedule()
		deleteAfter(scheduleTime?:0)
		true
	} else {
		false
	}
	
/**
 * <p>Delete all file inside a directory using Files.walk.</p>
 * <p>If file is not a directory do nothing</p>
 */
fun File.deleteDirContent() {
	if(isDirectory()) {
		Files.walk(toPath())
			 .filter({ path -> path != toPath() })
             .map(Path::toFile)
             .sorted({ p1, p2 -> -p1.compareTo(p2) })
             .forEach({it.delete()});
	}
}