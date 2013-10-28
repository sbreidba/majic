cmake_minimum_required(VERSION 2.8)
project(BoostConsumer)

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

add_definitions(-DBOOST_ALL_NO_LIB)

message (STATUS "CMAKE_INSTALL_PREFIX set to ${CMAKE_INSTALL_PREFIX}")

set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_STATIC_RUNTIME ON)
set(Boost_USE_MULTITHREADED ON)
find_package(Boost 1.46 COMPONENTS date_time filesystem regex system REQUIRED)

find_package(Boost 1.47 COMPONENTS filesystem REQUIRED)

include(CPack)
