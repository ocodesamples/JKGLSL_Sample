
cmake_minimum_required(VERSION 3.4.1)

include_directories(${CMAKE_SOURCE_DIR}/libOverDraw/include)

aux_source_directory(. J_LIST)
add_library(native-lib SHARED ${J_LIST})
add_subdirectory(libOverDraw)
find_library(log-lib log )
target_link_libraries(native-lib libOverDraw)