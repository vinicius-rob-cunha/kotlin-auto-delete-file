# kotlin-auto-delete-file

Extention for `java.util.File` class adding methods to delete after few seconds

## Usage

### Basic usage

    val file = File.createTempFile("text", ".tmp")
	file.deleteAfter(8) //delete file after 8 seconds

### How cancel a schedule

    file.cancelDeleteSchedule()
   
### How reset a schedule

    file.resetDeleteSchedule()
   
## Adicional method

To make more secure delete directories i created deleteDirContent

    file.deleteDirContent()
    
this method delete all files inside folder using `Files.walkFileTree`

## How it works

I created a singleton for provide ScheduledExecutorService to schedule deletion of files using a single deamon `Thread`.

When `deleteAfter` is called a new `ScheduledFuture` is created and set to a variable. The same happen with time.

When you try `cancel` or `reset` these variables is used to manipulate the schedule.

More on [source file](https://github.com/vinicius-rob-cunha/kotlin-auto-delete-file/blob/master/src/main/kotlin/br/com/vroc/autodeletefile/AutoDeleteFile.kt)
