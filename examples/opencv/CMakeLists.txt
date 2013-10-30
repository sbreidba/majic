cmake_minimum_required(VERSION 2.8)

project(opencvexample)

# check for PACKAGE_PATH and INSTALL_PATH
# If PACKAGE_PATH is given, it is appended to the the front of
# CMAKE_PREFIX_PATH
# If INSTALL_PATH is not given it gets set to
#   ${PACKAGE_PATH}/${PROJECT_NAME}-${PACKAGE_VERSION}
#   CMAKE_INSTALL_PREFIX is then set to INSTALL_PATH
if (DEFINED PACKAGE_PATH)
    set (PACKAGE_PATH ${PACKAGE_PATH} CACHE PATH "Package directory" FORCE)
    set (CMAKE_PREFIX_PATH ${PACKAGE_PATH} ${CMAKE_PREFIX_PATH})
    message (STATUS "CMAKE_PREFIX_PATH set to ${CMAKE_PREFIX_PATH}")
endif ()

if (NOT DEFINED INSTALL_PATH AND DEFINED PACKAGE_PATH)
    list (GET PACKAGE_PATH 0 TEMP_IPATH)
    if (NOT WIN32)
        set (INSTALL_PATH ${TEMP_IPATH} CACHE PATH "Install Directory" FORCE)
    else ()
        set (INSTALL_PATH ${TEMP_IPATH}/${PROJECT_NAME}-${CPACK_PACKAGE_VERSION}
            CACHE PATH "Install Directory" FORCE)
    endif ()
endif ()

if (DEFINED INSTALL_PATH)
    set (INSTALL_PATH ${INSTALL_PATH} CACHE PATH "Install Directory" FORCE)
    set (CMAKE_INSTALL_PREFIX ${INSTALL_PATH})
endif ()

message (STATUS "CMAKE_INSTALL_PREFIX set to ${CMAKE_INSTALL_PREFIX}")

# Set a default build type ("Release") if no build type is set.
# This is important for Linux, but not for Visual Studio users, since they
# can choose in the IDE.
if (NOT CMAKE_CONFIGURATION_TYPES AND NOT CMAKE_BUILD_TYPE)
   set (CMAKE_BUILD_TYPE Release)
endif ()

# The debug version of the library will be called VisionLib_d.
set (CMAKE_DEBUG_POSTFIX _d)

enable_testing()

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    CACHE PATH "Output directory for static libraries.")

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    CACHE PATH "Output directory for shared libraries.")

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
    CACHE PATH "Output directory for executables and DLL's.")

set(BUILD_SHARED_LIBS ON)

# Use solution folders in Visual Studio IDE
set_property(GLOBAL PROPERTY USE_FOLDERS ON)

# first time for making platform specific settings
if (MSVC)
    # XXX Skip: this is something that is not really project specific
    # and we may want to have it in a more central location so it may
    # be used across all projects
    add_definitions(-D_WIN32_WINNT=0x0601)
    add_definitions(-D_CRT_SECURE_NO_WARNINGS -D_SCL_SECURE_NO_WARNINGS)
    add_definitions(-DEIGEN_DONT_ALIGN_STATICALLY)
endif()

find_package(OpenCV REQUIRED)
message(STATUS "Using OpenCV version ${OpenCV_VERSION}")

include_directories(${OpenCV_INCLUDE_DIRS})

add_executable(${PROJECT_NAME} src/main.cpp)
target_link_libraries(${PROJECT_NAME} ${OpenCV_LIBS})
set_target_properties(${PROJECT_NAME} PROPERTIES DEBUG_POSTFIX ${CMAKE_DEBUG_POSTFIX})

#install(TARGETS ${TOOLS} DESTINATION bin)

include(CPack)
