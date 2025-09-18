package com.corridor.os.tools.debugger

import com.corridor.os.core.*
import com.corridor.os.tools.emulator.*
import java.io.File

/**
 * Optical Debugger for debugging optical programs
 */
class OpticalDebugger {
    
    private var programInstructions = mutableListOf<OpticalInstruction>()
    private var programCounter = 0
    private var registers = mutableMapOf<OpticalRegister, Long>()
    private var breakpoints = mutableSetOf<Int>()
    private var isRunning = false
    private var emulator: OpticalComputerEmulator? = null
    
    init {
        initializeRegisters()
    }
    
    private fun initializeRegisters() {
        OpticalRegister.values().forEach { register ->
            registers[register] = 0L
        }
    }
    
    fun loadProgram(binaryFile: String): Boolean {
        return try {
            val binaryData = File(binaryFile).readBytes()
            programInstructions = parseBinaryInstructions(binaryData)
            programCounter = 0
            
            println("Loaded program: ${programInstructions.size} instructions")
            true
            
        } catch (e: Exception) {
            println("Failed to load program: ${e.message}")
            false
        }
    }
    
    private fun parseBinaryInstructions(binaryData: ByteArray): MutableList<OpticalInstruction> {
        val instructions = mutableListOf<OpticalInstruction>()
        var offset = 0
        
        while (offset + 16 <= binaryData.size) { // 16 bytes per instruction
            // Parse opcode (4 bytes)
            val opcode = ((binaryData[offset + 3].toInt() and 0xFF) shl 24) or
                        ((binaryData[offset + 2].toInt() and 0xFF) shl 16) or
                        ((binaryData[offset + 1].toInt() and 0xFF) shl 8) or
                        (binaryData[offset].toInt() and 0xFF)
            
            // Parse wavelength (8 bytes)
            var wavelengthBits = 0L
            for (i in 0..7) {
                wavelengthBits = wavelengthBits or ((binaryData[offset + 4 + i].toLong() and 0xFF) shl (i * 8))
            }
            val wavelength = Double.fromBits(wavelengthBits)
            
            // Parse parallel lanes (4 bytes)
            val parallelLanes = ((binaryData[offset + 15].toInt() and 0xFF) shl 24) or
                              ((binaryData[offset + 14].toInt() and 0xFF) shl 16) or
                              ((binaryData[offset + 13].toInt() and 0xFF) shl 8) or
                              (binaryData[offset + 12].toInt() and 0xFF)
            
            val instruction = OpticalInstruction(
                opcode = opcode.toUInt(),
                wavelength = wavelength,
                parallelLanes = parallelLanes,
                x86Equivalent = getInstructionMnemonic(opcode.toUInt()),
                executionTimePs = 100L // Default execution time
            )
            
            instructions.add(instruction)
            offset += 16
        }
        
        return instructions
    }
    
    private fun getInstructionMnemonic(opcode: UInt): String {
        return when (opcode) {
            0x01u -> "OADD"
            0x02u -> "OSUB"
            0x03u -> "OMUL"
            0x04u -> "ODIV"
            0x10u -> "OMOV"
            0x11u -> "OLOAD"
            0x12u -> "OSTORE"
            0x20u -> "OJMP"
            0x21u -> "OJE"
            0x22u -> "OJNE"
            0x23u -> "OCALL"
            0x24u -> "ORET"
            0x30u -> "OAND"
            0x31u -> "OOR"
            0x32u -> "OXOR"
            0x33u -> "ONOT"
            0x40u -> "OWDM"
            0x41u -> "OWSEL"
            0x42u -> "OWAMP"
            0xF0u -> "OSYNC"
            0xFFu -> "OHALT"
            else -> "UNKNOWN"
        }
    }
    
    fun run() {
        if (programInstructions.isEmpty()) {
            println("No program loaded")
            return
        }
        
        println("Running program...")
        isRunning = true
        
        while (isRunning && programCounter < programInstructions.size) {
            if (breakpoints.contains(programCounter)) {
                println("Breakpoint hit at instruction $programCounter")
                showCurrentInstruction()
                break
            }
            
            executeCurrentInstruction()
            programCounter++
        }
        
        if (programCounter >= programInstructions.size) {
            println("Program completed")
        }
    }
    
    fun step() {
        if (programInstructions.isEmpty()) {
            println("No program loaded")
            return
        }
        
        if (programCounter >= programInstructions.size) {
            println("Program completed")
            return
        }
        
        showCurrentInstruction()
        executeCurrentInstruction()
        programCounter++
        
        if (programCounter >= programInstructions.size) {
            println("Program completed")
        }
    }
    
    private fun executeCurrentInstruction() {
        if (programCounter >= programInstructions.size) return
        
        val instruction = programInstructions[programCounter]
        
        // Simulate instruction execution
        when (instruction.opcode) {
            0x01u -> executeOpticalAdd(instruction)
            0x02u -> executeOpticalSub(instruction)
            0x03u -> executeOpticalMul(instruction)
            0x10u -> executeOpticalMove(instruction)
            0x20u -> executeOpticalJump(instruction)
            0xFFu -> {
                println("HALT instruction executed")
                isRunning = false
            }
            else -> executeGenericInstruction(instruction)
        }
        
        // Update optical system state
        updateOpticalState(instruction)
    }
    
    private fun executeOpticalAdd(instruction: OpticalInstruction) {
        // Simulate optical addition
        val result = registers[OpticalRegister.OAX]!! + registers[OpticalRegister.OBX]!!
        registers[OpticalRegister.OAX] = result
        
        println("Executed OADD: OAX = $result (${instruction.parallelLanes} lanes @ ${instruction.wavelength}nm)")
    }
    
    private fun executeOpticalSub(instruction: OpticalInstruction) {
        val result = registers[OpticalRegister.OAX]!! - registers[OpticalRegister.OBX]!!
        registers[OpticalRegister.OAX] = result
        
        println("Executed OSUB: OAX = $result (${instruction.parallelLanes} lanes @ ${instruction.wavelength}nm)")
    }
    
    private fun executeOpticalMul(instruction: OpticalInstruction) {
        val result = registers[OpticalRegister.OAX]!! * registers[OpticalRegister.OBX]!!
        registers[OpticalRegister.OAX] = result
        
        println("Executed OMUL: OAX = $result (${instruction.parallelLanes} lanes @ ${instruction.wavelength}nm)")
    }
    
    private fun executeOpticalMove(instruction: OpticalInstruction) {
        registers[OpticalRegister.OAX] = registers[OpticalRegister.OBX]!!
        
        println("Executed OMOV: OAX = ${registers[OpticalRegister.OAX]} (${instruction.parallelLanes} lanes @ ${instruction.wavelength}nm)")
    }
    
    private fun executeOpticalJump(instruction: OpticalInstruction) {
        // Simple jump simulation (would need more complex addressing in real implementation)
        val jumpTarget = registers[OpticalRegister.OBX]!!.toInt()
        if (jumpTarget in 0 until programInstructions.size) {
            programCounter = jumpTarget - 1 // -1 because it will be incremented
            println("Executed OJMP: jumped to instruction $jumpTarget")
        } else {
            println("Invalid jump target: $jumpTarget")
        }
    }
    
    private fun executeGenericInstruction(instruction: OpticalInstruction) {
        println("Executed ${instruction.x86Equivalent} (${instruction.parallelLanes} lanes @ ${instruction.wavelength}nm)")
    }
    
    private fun updateOpticalState(instruction: OpticalInstruction) {
        // Update optical system state based on instruction execution
        // This would involve wavelength channel utilization, power consumption, etc.
    }
    
    private fun showCurrentInstruction() {
        if (programCounter >= programInstructions.size) {
            println("Program counter beyond program end")
            return
        }
        
        val instruction = programInstructions[programCounter]
        println("[$programCounter] ${instruction.x86Equivalent} (opcode: ${instruction.opcode}, " +
                "wavelength: ${instruction.wavelength}nm, lanes: ${instruction.parallelLanes})")
    }
    
    fun setBreakpoint() {
        breakpoints.add(programCounter)
        println("Breakpoint set at instruction $programCounter")
    }
    
    fun setBreakpoint(address: Int) {
        if (address in 0 until programInstructions.size) {
            breakpoints.add(address)
            println("Breakpoint set at instruction $address")
        } else {
            println("Invalid breakpoint address: $address")
        }
    }
    
    fun removeBreakpoint(address: Int) {
        if (breakpoints.remove(address)) {
            println("Breakpoint removed from instruction $address")
        } else {
            println("No breakpoint at instruction $address")
        }
    }
    
    fun showRegisters() {
        println("\n=== Optical Registers ===")
        
        // General purpose registers
        println("General Purpose:")
        println("  OAX (${OpticalRegister.OAX.wavelengthNm}nm): ${registers[OpticalRegister.OAX]}")
        println("  OBX (${OpticalRegister.OBX.wavelengthNm}nm): ${registers[OpticalRegister.OBX]}")
        println("  OCX (${OpticalRegister.OCX.wavelengthNm}nm): ${registers[OpticalRegister.OCX]}")
        println("  ODX (${OpticalRegister.ODX.wavelengthNm}nm): ${registers[OpticalRegister.ODX]}")
        
        // WDM registers
        println("\nWavelength Division Multiplexing:")
        println("  WDM0 (${OpticalRegister.WDM0.wavelengthNm}nm): ${registers[OpticalRegister.WDM0]}")
        println("  WDM1 (${OpticalRegister.WDM1.wavelengthNm}nm): ${registers[OpticalRegister.WDM1]}")
        println("  WDM2 (${OpticalRegister.WDM2.wavelengthNm}nm): ${registers[OpticalRegister.WDM2]}")
        println("  WDM3 (${OpticalRegister.WDM3.wavelengthNm}nm): ${registers[OpticalRegister.WDM3]}")
        
        // Control registers
        println("\nControl:")
        println("  OPC (${OpticalRegister.OPC.wavelengthNm}nm): ${registers[OpticalRegister.OPC]} (Program Counter)")
        println("  OSP (${OpticalRegister.OSP.wavelengthNm}nm): ${registers[OpticalRegister.OSP]} (Stack Pointer)")
        println("  OSR (${OpticalRegister.OSR.wavelengthNm}nm): ${registers[OpticalRegister.OSR]} (Status Register)")
        
        println("\nCurrent Program Counter: $programCounter")
    }
    
    fun showMemory() {
        println("\n=== Memory State ===")
        println("Program Instructions: ${programInstructions.size}")
        println("Current PC: $programCounter")
        
        // Show instructions around current PC
        val start = maxOf(0, programCounter - 2)
        val end = minOf(programInstructions.size, programCounter + 3)
        
        println("\nInstructions:")
        for (i in start until end) {
            val marker = if (i == programCounter) ">" else " "
            val instruction = programInstructions[i]
            println("$marker [$i] ${instruction.x86Equivalent} (${instruction.wavelength}nm, ${instruction.parallelLanes} lanes)")
        }
        
        // Show breakpoints
        if (breakpoints.isNotEmpty()) {
            println("\nBreakpoints: ${breakpoints.sorted()}")
        }
    }
    
    fun showOpticalState() {
        println("\n=== Optical System State ===")
        
        // Show wavelength channel utilization
        println("Wavelength Channels:")
        val wavelengths = listOf(1550.0, 1551.0, 1552.0, 1553.0, 1554.0, 1555.0)
        wavelengths.forEach { wavelength ->
            val usage = calculateWavelengthUsage(wavelength)
            val bar = "█".repeat((usage * 20).toInt()) + "░".repeat(20 - (usage * 20).toInt())
            println("  ${wavelength}nm: [$bar] ${String.format("%.1f", usage * 100)}%")
        }
        
        // Show optical power levels
        println("\nOptical Power Levels:")
        println("  Total Input Power: 10.0 mW")
        println("  Total Output Power: 8.2 mW")
        println("  System Efficiency: 82.0%")
        
        // Show timing information
        println("\nTiming:")
        println("  Clock Frequency: 1.0 THz")
        println("  Average Execution Time: 150 ps")
        println("  Wavelength Coherence: 1.2 ns")
        
        // Show parallel processing
        println("\nParallel Processing:")
        val currentInstruction = if (programCounter < programInstructions.size) {
            programInstructions[programCounter]
        } else null
        
        if (currentInstruction != null) {
            println("  Active Lanes: ${currentInstruction.parallelLanes}/128")
            println("  Lane Utilization: ${String.format("%.1f", currentInstruction.parallelLanes / 128.0 * 100)}%")
        } else {
            println("  Active Lanes: 0/128")
            println("  Lane Utilization: 0.0%")
        }
    }
    
    private fun calculateWavelengthUsage(wavelength: Double): Double {
        // Calculate how much this wavelength is being used
        val instructionsUsingWavelength = programInstructions.count { 
            kotlin.math.abs(it.wavelength - wavelength) < 0.5 
        }
        return if (programInstructions.isNotEmpty()) {
            instructionsUsingWavelength.toDouble() / programInstructions.size
        } else 0.0
    }
    
    fun reset() {
        programCounter = 0
        initializeRegisters()
        isRunning = false
        println("Debugger reset")
    }
    
    fun getDebuggerState(): DebuggerState {
        return DebuggerState(
            programCounter = programCounter,
            isRunning = isRunning,
            totalInstructions = programInstructions.size,
            breakpoints = breakpoints.toList(),
            registers = registers.toMap()
        )
    }
}

/**
 * Debugger state information
 */
data class DebuggerState(
    val programCounter: Int,
    val isRunning: Boolean,
    val totalInstructions: Int,
    val breakpoints: List<Int>,
    val registers: Map<OpticalRegister, Long>
)




