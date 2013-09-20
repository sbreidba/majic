project(artidepTest)

cmake_minimum_required(VERSION 2.8)

set(GTEST_LIBS_DIR ${gtest_BINARY_DIR})
set(GTEST_INCLUDE_DIR ${gtest_SOURCE_DIR}/include)

include_directories(${GTEST_INCLUDE_DIR} ${COMMON_INCLUDES})

set(${PROJECT_NAME}_SRCS
    main.cpp
)

add_executable(${PROJECT_NAME} ${${PROJECT_NAME}_SRCS})
add_dependencies(${PROJECT_NAME} gtest gtest_main)

if(NOT WIN32)
	target_link_libraries(${PROJECT_NAME} pthread)
	target_link_libraries(${PROJECT_NAME}
		${GTEST_LIBS_DIR}/libgtest.a
		${GTEST_LIBS_DIR}/libgtest_main.a
	)
else()
	target_link_libraries(${PROJECT_NAME}
	       debug
               ${GTEST_LIBS_DIR}/DebugLibs/${CMAKE_FIND_LIBRARY_PREFIXES}gtest${CMAKE_FIND_LIBRARY_SUFFIXES}
	       optimized
               ${GTEST_LIBS_DIR}/ReleaseLibs/${CMAKE_FIND_LIBRARY_PREFIXES}gtest${CMAKE_FIND_LIBRARY_SUFFIXES}
	)
	target_link_libraries(${PROJECT_NAME}
	       debug
               ${GTEST_LIBS_DIR}/DebugLibs/${CMAKE_FIND_LIBRARY_PREFIXES}gtest_main${CMAKE_FIND_LIBRARY_SUFFIXES}
	       optimized
               ${GTEST_LIBS_DIR}/ReleaseLibs/${CMAKE_FIND_LIBRARY_PREFIXES}gtest_main${CMAKE_FIND_LIBRARY_SUFFIXES}
	)
endif()

target_link_libraries(${PROJECT_NAME}
    artidep
)

add_test(maintest ${PROJECT_NAME})
