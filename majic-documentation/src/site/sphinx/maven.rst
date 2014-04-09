==============
Working with Maven
==============

Terms
=====
Lifecycle
Plugin
Goal
Phase

Reference here: http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html

Lifecycle
=========

The correspondence between conventional cmake steps and maven steps
are:

.. todo:: validate this against current components.xml, possibly merge this information
   with the majicCMakeLifecycle file.
   
#. **generate-sources**: is the cmake configure step; ``cmake
   -D... pathToSource``

#. **compile**: is the build step; ``make``

#. **test**: is the unit test step; ``make test``

#. **prepare-package**: is the generate documentation and install
   artifacts; ``make doc install``

#. **package**: is where the installed artifacts from the previous
   step are put into a tarball for maven to use in its *install* step;
   ``make tarball``, where the *tarball* target is a clavin
   replacement of the conventional make package cpack step, which we
   want to avoid right now because it is too hard to get the cmake
   package cpack step correct.

The complete list of maven lifecycle phases (obtained executing
:command:`mvn` with no arguments) are listed below. The highlighted
phases are for the corresponding cmake steps discussed above.

|    validate
|    initialize
| **generate-sources**
|    process-sources
|    generate-resources
|    process-resources
| **compile**
|    process-classes
|    generate-test-sources
|    process-test-sources
|    generate-test-resources
|    process-test-resources
|    test-compile
|    process-test-classes
| **test**
| **prepare-package**
| **package**
|    pre-integration-test
|    integration-test
|    post-integration-test
|    verify
|    install
|    deploy
|    pre-clean
|    clean
|    post-clean
|    pre-site
|    site
|    post-site
|    site-deploy


.. _helpful-maven-commands:

Helpful Maven Commands
======================

Plugin Help
   When you run a command like mvn help:system, you are telling Maven to
   run the 'system' goal within the 'help' plugin. Almost all plugins have a 'help' goal.
   For example, to see all of the goals of the help plugin, try::

      mvn help:help

Effective POM
   The "effective pom" is the pom file with variable interpolation performed.
   building. The effective pom has all of the variables replaced with
   their actual value. The effective pom is analogous to the
   output of the pre-processor in a C/C++ compilation. To see the
   effective pom use::

      mvn help:effective-pom

Active Profiles
   Maven profiles enable optional or conditional elements within a pom file,
   such as what operating system or compiler is in use, or even whether to run
   other parts of the build cycle such as integration tests (unit tests are
   typically run on each invocation.)
   
   To see the active profiles use::

      mvn help:active-profiles

Show Environment
   Viewing the system environment (variables, tool paths, etc.) can be helpful
   for debugging. To get Maven's view of common environment information::

      mvn help:system

Looking for Dependency Updates
   To check if newer releases of dependencies are available, use::

      mvn versions:display-dependency-updates

   To see if releases or snapshots are available, use::

      mvn versions:display-dependency-updates -DallowSnapshots=true

   In general the Versions plugin can be quite helpful (the "set" goal is pretty handy).
   Take a look at the help goal to learn more.

Generating a Text View of Dependencies
   If you'd like to see what your project is dependent on (including transitive dependencies),
   run::

      mvn dependency:tree

   By default, Maven's dependency convergence strategies are used to filter the output. If you'd like
   to see *every* dependency, including those that were not selected by default, add the verbose flag::
   
      mvn dependency:tree -Dverbose=true

.. toctree::


