cmake_minimum_required(VERSION 2.8)
set(PROJECT_NAME artidep)

include_directories(${COMMON_INCLUDES})

set(${PROJECT_NAME}_HEADERS
    ${COMMON_INCLUDES}/artidep.h
)

set(${PROJECT_NAME}_SRCS
    artidep.cpp
)

add_library(${PROJECT_NAME} STATIC
    ${${PROJECT_NAME}_HEADERS}
    ${${PROJECT_NAME}_SRCS}
)

install(TARGETS ${PROJECT_NAME}
    ARCHIVE DESTINATION lib
)

install(FILES ${${PROJECT_NAME}_HEADERS}
    DESTINATION include
)

