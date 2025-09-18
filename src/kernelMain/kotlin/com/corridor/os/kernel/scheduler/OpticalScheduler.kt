package com.corridor.os.kernel.scheduler

import com.corridor.os.core.*
import kotlinx.cinterop.*

/**
 * Optical-aware task scheduler that leverages wavelength division multiplexing
 * and parallel optical processing capabilities
 */
class OpticalScheduler(private val processingUnit: OpticalProcessingUnit) {
    
    private val taskQueues = mutableMapOf<Int, MutableList<OpticalTask>>()
    private val runningTasks = mutableMapOf<Long, OpticalTask>()
    private val completedTasks = mutableListOf<OpticalTask>()
    
    private var nextTaskId = 1L
    private var schedulerTicks = 0L
    
    init {
        // Initialize priority queues for different wavelength channels
        for (priority in 0 until 8) {
            taskQueues[priority] = mutableListOf()
        }
        
        println("[SCHEDULER] Optical scheduler initialized")
        println("[SCHEDULER] Available wavelength channels: ${processingUnit.wavelengthChannels}")
        println("[SCHEDULER] Parallel processing factor: ${processingUnit.parallelismFactor}")
    }
    
    /**
     * Schedule a new task for execution
     */
    fun scheduleTask(
        processId: Long,
        instructions: List<OpticalInstruction>,
        priority: Int = 4,
        wavelengthPreference: Double? = null
    ): Long {
        val taskId = nextTaskId++
        
        val task = OpticalTask(
            taskId = taskId,
            processId = processId,
            instructions = instructions,
            priority = priority.coerceIn(0, 7),
            preferredWavelength = wavelengthPreference ?: assignOptimalWavelength(),
            state = TaskState.READY,
            creationTime = System.nanoTime(),
            estimatedExecutionTime = estimateExecutionTime(instructions)
        )
        
        taskQueues[task.priority]?.add(task)
        println("[SCHEDULER] Scheduled task $taskId for process $processId (priority ${task.priority})")
        
        return taskId
    }
    
    /**
     * Main scheduler tick - called by kernel main loop
     */
    fun tick() {
        schedulerTicks++
        
        // Process completed tasks
        processCompletedTasks()
        
        // Schedule new tasks if processing units are available
        scheduleReadyTasks()
        
        // Update running task states
        updateRunningTasks()
        
        // Perform wavelength optimization every 1000 ticks
        if (schedulerTicks % 1000L == 0L) {
            optimizeWavelengthAllocation()
        }
    }
    
    private fun processCompletedTasks() {
        val iterator = runningTasks.iterator()
        while (iterator.hasNext()) {
            val (taskId, task) = iterator.next()
            
            if (task.state == TaskState.COMPLETED) {
                task.completionTime = System.nanoTime()
                completedTasks.add(task)
                iterator.remove()
                
                println("[SCHEDULER] Task $taskId completed in ${task.getExecutionDuration()}ns")
            }
        }
    }
    
    private fun scheduleReadyTasks() {
        // Check if we have available processing capacity
        val availableChannels = processingUnit.wavelengthChannels - runningTasks.size
        if (availableChannels <= 0) return
        
        var scheduledCount = 0
        
        // Schedule tasks by priority (higher priority = lower number)
        for (priority in 0 until 8) {
            if (scheduledCount >= availableChannels) break
            
            val queue = taskQueues[priority] ?: continue
            val tasksToSchedule = minOf(queue.size, availableChannels - scheduledCount)
            
            repeat(tasksToSchedule) {
                if (queue.isNotEmpty()) {
                    val task = queue.removeAt(0)
                    startTask(task)
                    scheduledCount++
                }
            }
        }
    }
    
    private fun startTask(task: OpticalTask) {
        task.state = TaskState.RUNNING
        task.startTime = System.nanoTime()
        task.assignedWavelength = selectOptimalWavelength(task)
        
        runningTasks[task.taskId] = task
        
        println("[SCHEDULER] Started task ${task.taskId} on wavelength ${task.assignedWavelength}nm")
    }
    
    private fun updateRunningTasks() {
        runningTasks.values.forEach { task ->
            updateTaskProgress(task)
        }
    }
    
    private fun updateTaskProgress(task: OpticalTask) {
        val currentTime = System.nanoTime()
        val elapsedTime = currentTime - (task.startTime ?: currentTime)
        
        // Simulate task execution progress
        if (elapsedTime >= task.estimatedExecutionTime) {
            task.state = TaskState.COMPLETED
            task.executedInstructions = task.instructions.size
        } else {
            // Update progress based on elapsed time
            val progress = elapsedTime.toDouble() / task.estimatedExecutionTime.toDouble()
            task.executedInstructions = (task.instructions.size * progress).toInt()
        }
    }
    
    private fun assignOptimalWavelength(): Double {
        // Assign wavelength based on current load distribution
        val baseWavelength = OpticalConstants.DEFAULT_WAVELENGTH_NM
        val channelSpacing = 0.8 // 0.8nm channel spacing (ITU-T grid)
        
        // Find least used wavelength channel
        val channelUsage = mutableMapOf<Int, Int>()
        runningTasks.values.forEach { task ->
            val channel = ((task.assignedWavelength ?: baseWavelength) - baseWavelength) / channelSpacing
            channelUsage[channel.toInt()] = (channelUsage[channel.toInt()] ?: 0) + 1
        }
        
        val leastUsedChannel = (0 until processingUnit.wavelengthChannels)
            .minByOrNull { channelUsage[it] ?: 0 } ?: 0
        
        return baseWavelength + leastUsedChannel * channelSpacing
    }
    
    private fun selectOptimalWavelength(task: OpticalTask): Double {
        // Consider task preference and system optimization
        return task.preferredWavelength ?: assignOptimalWavelength()
    }
    
    private fun estimateExecutionTime(instructions: List<OpticalInstruction>): Long {
        // Estimate execution time based on instruction complexity and parallelism
        var totalTime = 0L
        
        instructions.forEach { instruction ->
            // Base execution time from instruction
            var instructionTime = instruction.executionTimePs
            
            // Apply parallelism factor
            val parallelismBenefit = minOf(instruction.parallelLanes, processingUnit.parallelismFactor)
            instructionTime = instructionTime / parallelismBenefit
            
            totalTime += instructionTime
        }
        
        return totalTime * 1000 // Convert to nanoseconds
    }
    
    private fun optimizeWavelengthAllocation() {
        // Analyze wavelength usage patterns and optimize allocation
        val wavelengthUsage = mutableMapOf<Double, Int>()
        
        runningTasks.values.forEach { task ->
            val wavelength = task.assignedWavelength ?: return@forEach
            wavelengthUsage[wavelength] = (wavelengthUsage[wavelength] ?: 0) + 1
        }
        
        // Identify over-utilized wavelengths
        val averageUsage = wavelengthUsage.values.average()
        val overUtilized = wavelengthUsage.filter { it.value > averageUsage * 1.5 }
        
        if (overUtilized.isNotEmpty()) {
            println("[SCHEDULER] Optimizing wavelength allocation for ${overUtilized.size} channels")
        }
    }
    
    /**
     * Get scheduler statistics
     */
    fun getSchedulerStatistics(): OpticalSchedulerStatistics {
        val totalTasks = completedTasks.size + runningTasks.size + 
                        taskQueues.values.sumOf { it.size }
        
        val averageExecutionTime = if (completedTasks.isNotEmpty()) {
            completedTasks.map { it.getExecutionDuration() }.average()
        } else 0.0
        
        val wavelengthUtilization = runningTasks.size.toDouble() / processingUnit.wavelengthChannels.toDouble()
        
        return OpticalSchedulerStatistics(
            totalTasksScheduled = totalTasks,
            runningTasks = runningTasks.size,
            completedTasks = completedTasks.size,
            queuedTasks = taskQueues.values.sumOf { it.size },
            averageExecutionTimeNs = averageExecutionTime,
            wavelengthUtilization = wavelengthUtilization,
            schedulerTicks = schedulerTicks
        )
    }
    
    /**
     * Cancel a scheduled task
     */
    fun cancelTask(taskId: Long): Boolean {
        // Check running tasks
        runningTasks[taskId]?.let { task ->
            task.state = TaskState.CANCELLED
            runningTasks.remove(taskId)
            return true
        }
        
        // Check queued tasks
        taskQueues.values.forEach { queue ->
            val task = queue.find { it.taskId == taskId }
            if (task != null) {
                queue.remove(task)
                task.state = TaskState.CANCELLED
                return true
            }
        }
        
        return false
    }
}

/**
 * Optical task representation
 */
data class OpticalTask(
    val taskId: Long,
    val processId: Long,
    val instructions: List<OpticalInstruction>,
    val priority: Int,
    val preferredWavelength: Double?,
    var state: TaskState,
    val creationTime: Long,
    val estimatedExecutionTime: Long,
    var startTime: Long? = null,
    var completionTime: Long? = null,
    var assignedWavelength: Double? = null,
    var executedInstructions: Int = 0
) {
    fun getExecutionDuration(): Long {
        return if (startTime != null && completionTime != null) {
            completionTime!! - startTime!!
        } else 0L
    }
}

/**
 * Task execution states
 */
enum class TaskState {
    READY,
    RUNNING,
    COMPLETED,
    CANCELLED,
    BLOCKED
}

/**
 * Scheduler performance statistics
 */
data class OpticalSchedulerStatistics(
    val totalTasksScheduled: Int,
    val runningTasks: Int,
    val completedTasks: Int,
    val queuedTasks: Int,
    val averageExecutionTimeNs: Double,
    val wavelengthUtilization: Double,
    val schedulerTicks: Long
)

