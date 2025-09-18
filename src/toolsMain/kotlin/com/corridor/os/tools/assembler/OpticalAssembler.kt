package com.corridor.os.tools.assembler

import com.corridor.os.core.*
import java.io.File

/**
 * Optical Assembly Language Assembler
 * Converts optical assembly language to optical machine code
 */
class OpticalAssembler {
    
    private val instructionSet = mutableMapOf<String, InstructionTemplate>()
    private val labels = mutableMapOf<String, Int>()
    private val symbols = mutableMapOf<String, Int>()
    
    init {
        initializeInstructionSet()
    }
    
    private fun initializeInstructionSet() {
        // Arithmetic instructions
        instructionSet["OADD"] = InstructionTemplate(0x01u, 2, "Optical Add")
        instructionSet["OSUB"] = InstructionTemplate(0x02u, 2, "Optical Subtract")
        instructionSet["OMUL"] = InstructionTemplate(0x03u, 2, "Optical Multiply")
        instructionSet["ODIV"] = InstructionTemplate(0x04u, 2, "Optical Divide")
        
        // Data movement instructions
        instructionSet["OMOV"] = InstructionTemplate(0x10u, 2, "Optical Move")
        instructionSet["OLOAD"] = InstructionTemplate(0x11u, 2, "Optical Load")
        instructionSet["OSTORE"] = InstructionTemplate(0x12u, 2, "Optical Store")
        
        // Control flow instructions
        instructionSet["OJMP"] = InstructionTemplate(0x20u, 1, "Optical Jump")
        instructionSet["OJE"] = InstructionTemplate(0x21u, 3, "Optical Jump if Equal")
        instructionSet["OJNE"] = InstructionTemplate(0x22u, 3, "Optical Jump if Not Equal")
        instructionSet["OCALL"] = InstructionTemplate(0x23u, 1, "Optical Call")
        instructionSet["ORET"] = InstructionTemplate(0x24u, 0, "Optical Return")
        
        // Logical instructions
        instructionSet["OAND"] = InstructionTemplate(0x30u, 2, "Optical AND")
        instructionSet["OOR"] = InstructionTemplate(0x31u, 2, "Optical OR")
        instructionSet["OXOR"] = InstructionTemplate(0x32u, 2, "Optical XOR")
        instructionSet["ONOT"] = InstructionTemplate(0x33u, 1, "Optical NOT")
        
        // Wavelength-specific instructions
        instructionSet["OWDM"] = InstructionTemplate(0x40u, 3, "Wavelength Division Multiplex")
        instructionSet["OWSEL"] = InstructionTemplate(0x41u, 2, "Wavelength Select")
        instructionSet["OWAMP"] = InstructionTemplate(0x42u, 2, "Wavelength Amplify")
        
        // System instructions
        instructionSet["OSYNC"] = InstructionTemplate(0xF0u, 0, "Optical Synchronize")
        instructionSet["OHALT"] = InstructionTemplate(0xFFu, 0, "Halt System")
    }
    
    fun assemble(inputFile: String, outputFile: String, verbose: Boolean = false): AssemblyResult {
        try {
            val sourceLines = File(inputFile).readLines()
            val instructions = mutableListOf<OpticalInstruction>()
            val errors = mutableListOf<AssemblyError>()
            
            if (verbose) {
                println("Parsing assembly file: $inputFile")
            }
            
            // First pass: collect labels and symbols
            var lineNumber = 0
            var instructionAddress = 0
            
            sourceLines.forEach { line ->
                lineNumber++
                val cleanLine = line.trim().split(";")[0].trim() // Remove comments
                
                if (cleanLine.isNotEmpty()) {
                    if (cleanLine.endsWith(":")) {
                        // Label definition
                        val label = cleanLine.dropLast(1)
                        labels[label] = instructionAddress
                        if (verbose) {
                            println("Found label: $label at address $instructionAddress")
                        }
                    } else if (!cleanLine.startsWith(".")) {
                        // Regular instruction (not directive)
                        instructionAddress++
                    }
                }
            }
            
            // Second pass: generate instructions
            lineNumber = 0
            instructionAddress = 0
            
            sourceLines.forEach { line ->
                lineNumber++
                val cleanLine = line.trim().split(";")[0].trim()
                
                if (cleanLine.isNotEmpty() && !cleanLine.endsWith(":") && !cleanLine.startsWith(".")) {
                    try {
                        val instruction = parseLine(cleanLine, instructionAddress)
                        instructions.add(instruction)
                        instructionAddress++
                        
                        if (verbose) {
                            println("Line $lineNumber: ${instruction.x86Equivalent} -> ${instruction.opcode}")
                        }
                        
                    } catch (e: Exception) {
                        errors.add(AssemblyError(lineNumber, e.message ?: "Unknown error"))
                    }
                }
            }
            
            if (errors.isNotEmpty()) {
                return AssemblyResult(
                    isSuccess = false,
                    errorMessage = "Assembly failed with ${errors.size} errors",
                    errors = errors
                )
            }
            
            // Write binary output
            writeBinaryFile(outputFile, instructions)
            
            return AssemblyResult(
                isSuccess = true,
                instructionCount = instructions.size,
                codeSize = instructions.size * 16, // Assume 16 bytes per instruction
                outputFile = outputFile
            )
            
        } catch (e: Exception) {
            return AssemblyResult(
                isSuccess = false,
                errorMessage = "Assembly failed: ${e.message}",
                errors = listOf(AssemblyError(0, e.message ?: "Unknown error"))
            )
        }
    }
    
    private fun parseLine(line: String, address: Int): OpticalInstruction {
        val parts = line.split(Regex("\\s+|,")).filter { it.isNotEmpty() }
        
        if (parts.isEmpty()) {
            throw IllegalArgumentException("Empty instruction")
        }
        
        val mnemonic = parts[0].uppercase()
        val template = instructionSet[mnemonic]
            ?: throw IllegalArgumentException("Unknown instruction: $mnemonic")
        
        val operands = parts.drop(1)
        
        if (operands.size != template.operandCount) {
            throw IllegalArgumentException("$mnemonic expects ${template.operandCount} operands, got ${operands.size}")
        }
        
        // Determine optimal wavelength and parallelism based on instruction type
        val wavelength = determineOptimalWavelength(mnemonic, operands)
        val parallelLanes = determineParallelLanes(mnemonic, operands)
        val executionTime = estimateExecutionTime(mnemonic, parallelLanes)
        
        return OpticalInstruction(
            opcode = template.opcode,
            wavelength = wavelength,
            parallelLanes = parallelLanes,
            x86Equivalent = mnemonic,
            executionTimePs = executionTime
        )
    }
    
    private fun determineOptimalWavelength(mnemonic: String, operands: List<String>): Double {
        return when (mnemonic) {
            "OADD", "OSUB" -> 1550.0 // Arithmetic operations on primary wavelength
            "OMUL", "ODIV" -> 1551.0 // Complex operations on secondary wavelength
            "OMOV", "OLOAD", "OSTORE" -> 1552.0 // Data movement operations
            "OJMP", "OCALL", "ORET" -> 1553.0 // Control flow operations
            "OAND", "OOR", "OXOR" -> 1554.0 // Logical operations
            "OWDM" -> parseWavelength(operands.getOrNull(0)) ?: 1555.0
            else -> OpticalConstants.DEFAULT_WAVELENGTH_NM
        }
    }
    
    private fun determineParallelLanes(mnemonic: String, operands: List<String>): Int {
        return when (mnemonic) {
            "OADD", "OSUB" -> 32 // High parallelism for simple arithmetic
            "OMUL", "ODIV" -> 16 // Medium parallelism for complex arithmetic
            "OMOV", "OLOAD", "OSTORE" -> 64 // Very high parallelism for data movement
            "OJMP", "OCALL", "ORET" -> 1 // Sequential for control flow
            "OAND", "OOR", "OXOR" -> 32 // High parallelism for logical operations
            "OWDM" -> parseParallelLanes(operands.getOrNull(1)) ?: 8
            else -> 16 // Default parallelism
        }
    }
    
    private fun estimateExecutionTime(mnemonic: String, parallelLanes: Int): Long {
        val baseTime = when (mnemonic) {
            "OADD", "OSUB" -> 100L // 100ps for simple arithmetic
            "OMUL" -> 200L // 200ps for multiplication
            "ODIV" -> 500L // 500ps for division
            "OMOV", "OLOAD", "OSTORE" -> 50L // 50ps for data movement
            "OJMP", "OCALL" -> 150L // 150ps for control flow
            "ORET" -> 100L // 100ps for return
            "OAND", "OOR", "OXOR" -> 80L // 80ps for logical operations
            "OWDM" -> 300L // 300ps for wavelength operations
            else -> 200L // Default execution time
        }
        
        // Apply parallelism benefit (more lanes = faster execution)
        return maxOf(baseTime / (parallelLanes / 8), 50L) // Minimum 50ps
    }
    
    private fun parseWavelength(operand: String?): Double? {
        if (operand == null) return null
        
        return when {
            operand.endsWith("nm") -> operand.dropLast(2).toDoubleOrNull()
            operand.toDoubleOrNull() != null -> operand.toDouble()
            else -> null
        }
    }
    
    private fun parseParallelLanes(operand: String?): Int? {
        return operand?.toIntOrNull()?.coerceIn(1, 128)
    }
    
    private fun writeBinaryFile(outputFile: String, instructions: List<OpticalInstruction>) {
        File(outputFile).writeBytes(
            instructions.flatMap { instruction ->
                // Simple binary format: opcode (4 bytes) + wavelength (8 bytes) + lanes (4 bytes)
                listOf(
                    instruction.opcode.toByte(),
                    (instruction.opcode shr 8).toByte(),
                    (instruction.opcode shr 16).toByte(),
                    (instruction.opcode shr 24).toByte()
                ) + instruction.wavelength.toBits().let { bits ->
                    (0..7).map { i -> ((bits shr (i * 8)) and 0xFF).toByte() }
                } + listOf(
                    instruction.parallelLanes.toByte(),
                    (instruction.parallelLanes shr 8).toByte(),
                    (instruction.parallelLanes shr 16).toByte(),
                    (instruction.parallelLanes shr 24).toByte()
                )
            }.toByteArray()
        )
    }
}

/**
 * Instruction template for assembly
 */
data class InstructionTemplate(
    val opcode: UInt,
    val operandCount: Int,
    val description: String
)

/**
 * Assembly result
 */
data class AssemblyResult(
    val isSuccess: Boolean,
    val instructionCount: Int = 0,
    val codeSize: Int = 0,
    val outputFile: String = "",
    val errorMessage: String = "",
    val errors: List<AssemblyError> = emptyList()
)

/**
 * Assembly error
 */
data class AssemblyError(
    val line: Int,
    val message: String
)




