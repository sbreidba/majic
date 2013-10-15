cmake_minimum_required(VERSION 2.8)
project(BoostConsumer)

find_package(Boost 1.47 COMPONENTS filesystem REQUIRED)

include(CPack)
