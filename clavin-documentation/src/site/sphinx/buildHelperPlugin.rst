===============================
Majic Build Helper Maven Plugin
===============================

Todo
====

#. Aggregate build from:

   - dependency tar balls from Artifactory

   - source present in aggregator subdirectories

#. need to be able to chain a series of cmake goals to a list of
   configurations to build. In the cmake configuration step, the user
   should specify the ``CMAKE_BUILD_TYPE``; e.g.,
   ``-DCMAKE_BUILD_TYPE="Debug;Release;RELWITHDEBINFO"``. Then for
   each build type, the user will need to make the ``all`` and
   ``install`` targets at a minimum. Building of optional targets of
   ``test`` and ``doc`` may be a function of both the configuration
   type and the operating system (i.e., Linux or Windows).

   Config=Debug, Os=Windows: 
      make all install

   Config=Release, Os=Windows: 
      make all test doc install

   Config=Debug, Os=Linux: 
      make all test install

   Config=Release, Os=Windows: 
      make all test doc install

   I do not know if this is best down with *ALL* complexity being pushed to
   the plugin or with a clever use of hooking maven goals to lifecycle
   phases. For example if all complexity were pushed to the plugin,
   then we could end up with something similar to::

      cmake-build 
         os=win 
         config=Debug,Release 
         targets=(all, install; all, test, doc, install)

      cmake-build 
         os=linux
         config=Debug,Release 
         targets=(all, test, install; all, test, doc, install)

   But the above would be not really work because cmake for the Linux
   makefile generator on Linux works with only a single configuration
   value whereas it works for multiple configuration values with the
   msvc generator. Furthermore, a good cmake build should provide the
   option for turning on/off various targets (i.e., test, examples,
   etc.) and that is also a configuration choice. 

#. Aggregate download git source for all modules
   
#. Need to think through consequences of using a map for cmake
   variables. How do we shared settings across modules when using a
   map? 





