

include_directories(include)

aux_source_directory(./src DRAW_LIST)

add_library(libOverDraw  SHARED ${DRAW_LIST})

find_library(log-lib log )


set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

if (${ANDROID_PLATFORM_LEVEL} LESS 12)
  message(FATAL_ERROR "OpenGL 3 is not supported before API level 12 \
                      (currently using ${ANDROID_PLATFORM_LEVEL}).")
  return()
elseif (${ANDROID_PLATFORM_LEVEL} LESS 18)
     message(FATAL_ERROR "OpenGL 3 is not supported before API level 18 \
                          (currently using ${ANDROID_PLATFORM_LEVEL}).")
    return()
else ()
  set(OPENGL_LIB GLESv3)
endif (${ANDROID_PLATFORM_LEVEL} LESS 12)


target_link_libraries(libOverDraw ${log-lib})
target_link_libraries(libOverDraw ${OPENGL_LIB} EGL android m)