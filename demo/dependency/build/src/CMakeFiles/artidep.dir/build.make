# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 2.8

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /mnt/hgfs/Devel/avaa/pkg-Locktight/exports/bin/cmake

# The command to remove a file.
RM = /mnt/hgfs/Devel/avaa/pkg-Locktight/exports/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The program to use to edit the cache.
CMAKE_EDIT_COMMAND = /mnt/hgfs/Devel/avaa/pkg-Locktight/exports/bin/ccmake

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /mnt/hgfs/Devel/clavin/dependency

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /mnt/hgfs/Devel/clavin/dependency/build

# Include any dependencies generated for this target.
include src/CMakeFiles/artidep.dir/depend.make

# Include the progress variables for this target.
include src/CMakeFiles/artidep.dir/progress.make

# Include the compile flags for this target's objects.
include src/CMakeFiles/artidep.dir/flags.make

src/CMakeFiles/artidep.dir/artidep.cpp.o: src/CMakeFiles/artidep.dir/flags.make
src/CMakeFiles/artidep.dir/artidep.cpp.o: ../src/artidep.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /mnt/hgfs/Devel/clavin/dependency/build/CMakeFiles $(CMAKE_PROGRESS_1)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object src/CMakeFiles/artidep.dir/artidep.cpp.o"
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/artidep.dir/artidep.cpp.o -c /mnt/hgfs/Devel/clavin/dependency/src/artidep.cpp

src/CMakeFiles/artidep.dir/artidep.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/artidep.dir/artidep.cpp.i"
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /mnt/hgfs/Devel/clavin/dependency/src/artidep.cpp > CMakeFiles/artidep.dir/artidep.cpp.i

src/CMakeFiles/artidep.dir/artidep.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/artidep.dir/artidep.cpp.s"
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /mnt/hgfs/Devel/clavin/dependency/src/artidep.cpp -o CMakeFiles/artidep.dir/artidep.cpp.s

src/CMakeFiles/artidep.dir/artidep.cpp.o.requires:
.PHONY : src/CMakeFiles/artidep.dir/artidep.cpp.o.requires

src/CMakeFiles/artidep.dir/artidep.cpp.o.provides: src/CMakeFiles/artidep.dir/artidep.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/artidep.dir/build.make src/CMakeFiles/artidep.dir/artidep.cpp.o.provides.build
.PHONY : src/CMakeFiles/artidep.dir/artidep.cpp.o.provides

src/CMakeFiles/artidep.dir/artidep.cpp.o.provides.build: src/CMakeFiles/artidep.dir/artidep.cpp.o

# Object files for target artidep
artidep_OBJECTS = \
"CMakeFiles/artidep.dir/artidep.cpp.o"

# External object files for target artidep
artidep_EXTERNAL_OBJECTS =

src/libartidep.a: src/CMakeFiles/artidep.dir/artidep.cpp.o
src/libartidep.a: src/CMakeFiles/artidep.dir/build.make
src/libartidep.a: src/CMakeFiles/artidep.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --red --bold "Linking CXX static library libartidep.a"
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && $(CMAKE_COMMAND) -P CMakeFiles/artidep.dir/cmake_clean_target.cmake
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/artidep.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
src/CMakeFiles/artidep.dir/build: src/libartidep.a
.PHONY : src/CMakeFiles/artidep.dir/build

src/CMakeFiles/artidep.dir/requires: src/CMakeFiles/artidep.dir/artidep.cpp.o.requires
.PHONY : src/CMakeFiles/artidep.dir/requires

src/CMakeFiles/artidep.dir/clean:
	cd /mnt/hgfs/Devel/clavin/dependency/build/src && $(CMAKE_COMMAND) -P CMakeFiles/artidep.dir/cmake_clean.cmake
.PHONY : src/CMakeFiles/artidep.dir/clean

src/CMakeFiles/artidep.dir/depend:
	cd /mnt/hgfs/Devel/clavin/dependency/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /mnt/hgfs/Devel/clavin/dependency /mnt/hgfs/Devel/clavin/dependency/src /mnt/hgfs/Devel/clavin/dependency/build /mnt/hgfs/Devel/clavin/dependency/build/src /mnt/hgfs/Devel/clavin/dependency/build/src/CMakeFiles/artidep.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : src/CMakeFiles/artidep.dir/depend

