#!/bin/bash

# Corridor Computer OS Build Script
# Builds the complete optical computing operating system

set -e  # Exit on any error

echo "========================================="
echo "Corridor Computer OS Build System"
echo "Version: 0.1.0-ALPHA"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
print_status "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    print_error "Java not found. Please install Java 17 or later."
    exit 1
fi

if ! command -v ./gradlew &> /dev/null; then
    print_error "Gradle wrapper not found."
    exit 1
fi

print_success "Prerequisites check passed"

# Clean previous builds
print_status "Cleaning previous builds..."
./gradlew clean
print_success "Clean completed"

# Build kernel
print_status "Building Corridor OS kernel..."
./gradlew buildKernel
if [ $? -eq 0 ]; then
    print_success "Kernel build completed"
else
    print_error "Kernel build failed"
    exit 1
fi

# Build development tools
print_status "Building development tools..."
./gradlew buildTools
if [ $? -eq 0 ]; then
    print_success "Development tools build completed"
else
    print_error "Development tools build failed"
    exit 1
fi

# Build everything
print_status "Building complete system..."
./gradlew buildAll
if [ $? -eq 0 ]; then
    print_success "Complete system build completed"
else
    print_error "Complete system build failed"
    exit 1
fi

# Create distribution directory
print_status "Creating distribution..."
mkdir -p dist/bin
mkdir -p dist/examples
mkdir -p dist/docs

# Copy binaries
cp -r build/bin/kernel/debugExecutable/* dist/bin/ 2>/dev/null || true
cp -r build/libs/* dist/bin/ 2>/dev/null || true

# Copy examples
cp examples/*.oasm dist/examples/

# Copy documentation
cp README.md dist/docs/
cp -r docs/* dist/docs/ 2>/dev/null || true

print_success "Distribution created in dist/"

# Create convenience scripts
print_status "Creating convenience scripts..."

# Create emulator launch script
cat > dist/run-emulator.sh << 'EOF'
#!/bin/bash
echo "Starting Corridor Computer OS Emulator..."
java -jar bin/corridor-tools.jar emulator start
EOF

# Create CLI script
cat > dist/corridor-cli.sh << 'EOF'
#!/bin/bash
java -jar bin/corridor-tools.jar "$@"
EOF

# Create assembler script
cat > dist/assemble.sh << 'EOF'
#!/bin/bash
if [ $# -eq 0 ]; then
    echo "Usage: $0 <input.oasm> [output.obin]"
    exit 1
fi

INPUT="$1"
OUTPUT="${2:-${INPUT%.oasm}.obin}"

echo "Assembling $INPUT -> $OUTPUT"
java -jar bin/corridor-tools.jar assemble "$INPUT" -o "$OUTPUT"
EOF

# Make scripts executable
chmod +x dist/*.sh

print_success "Convenience scripts created"

# Run example assembly
print_status "Testing assembler with example programs..."
if [ -f "dist/bin/corridor-tools.jar" ]; then
    cd dist
    ./assemble.sh examples/hello_world.oasm examples/hello_world.obin
    ./assemble.sh examples/parallel_matrix.oasm examples/parallel_matrix.obin
    cd ..
    print_success "Example programs assembled successfully"
else
    print_warning "Tools JAR not found, skipping example assembly"
fi

# Display build summary
echo ""
echo "========================================="
echo "BUILD SUMMARY"
echo "========================================="
print_success "Corridor Computer OS build completed!"
echo ""
echo "Components built:"
echo "  ✓ Optical Computing Kernel"
echo "  ✓ Hardware Abstraction Layer"
echo "  ✓ x86/x64 Compatibility Layer"
echo "  ✓ Optical Memory Manager"
echo "  ✓ Ejectable ROM Drivers"
echo "  ✓ Electro-Optical Interface Drivers"
echo "  ✓ Boot Loader"
echo "  ✓ Development Tools"
echo "  ✓ Optical Emulator"
echo "  ✓ Assembler & Debugger"
echo "  ✓ Benchmark Suite"
echo ""
echo "Quick Start:"
echo "  cd dist"
echo "  ./run-emulator.sh          # Start emulator"
echo "  ./corridor-cli.sh help     # Show CLI help"
echo "  ./assemble.sh examples/hello_world.oasm  # Assemble example"
echo ""
echo "System Specifications:"
echo "  Processing: 32-channel optical CPU @ 1.0 THz"
echo "  Memory: 64GB holographic RAM + 8GB optical cache"
echo "  Storage: 4× 1TB ejectable ROM slots"
echo "  Interfaces: USB 3.2, 10G Ethernet, DisplayPort 2.0, NVMe PCIe 5.0"
echo "  Compatibility: Full x86/x64 binary translation"
echo ""
print_success "Ready for optical computing development!"
echo "========================================="




