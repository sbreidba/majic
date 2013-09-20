# Credit to https://github.com/snikulov/google-test-examples for some basic
# setup
project(artidep)

cmake_minimum_required(VERSION 2.8)

enable_testing()

set(VTCMAKE /usr/local/vtcmake)
set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR} ${VTCMAKE} ${CMAKE_MODULE_PATH})
set(CMAKE_INSTALL_PREFIX exports)
set(CMAKE_PREFIX_PATH build/exports)
set(COMMON_INCLUDES ${PROJECT_SOURCE_DIR}/include)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
#set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
#set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

#include(VtArtifactoryPackager)

add_subdirectory(src)

add_subdirectory(${GOOGLETEST_ROOT})
add_subdirectory(test)

set(CPACK_GENERATOR "TBZ2")
include(CPack)
