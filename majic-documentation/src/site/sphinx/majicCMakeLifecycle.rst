====================
Packaging with Majic
====================

Maven has a number of packaging schemes available by default. The most commonly-used,
"jar", is used to compile Java files into ``.class`` files and package them into a ``.jar``
file with a manifest. But you'll notice that most of the time, nothing needs to be
done to configure the Java compilation and JAR packaging steps - they are simply
absent from the POM file entirely.

This is because the <packaging> element in the POM file is not simply descriptive;
it defines the set of plugins that will be used to execute the build. Each of those
plugins has a default configuration that defines the conventional build configuration,
e.g. java source code is in ``src/java/main``, java test code is in ``src/java/test``, etc.
These can be overridden as desired, but in general the simplest approach is to organize
the code to fit the conventional structure.

Majic defines customized lifecycle bindings designed to support CMake projects. It's
``<packaging>`` is ``majic-cmake`` and can be found in
:download:`components.xml <../../../../build-helper-maven-plugin/src/main/resources/META-INF/plexus/components.xml>`.

The relevant part of that file is the following:

.. literalinclude:: ../../../../build-helper-maven-plugin/src/main/resources/META-INF/plexus/components.xml
   :language: xml
   :start-after: <!-- majic-documentation:lifecyclemapping -->
   :end-before: <!-- majic-documentation:lifecyclemapping -->

The role-hint defines the ``<packaging>``, and ``<phases>`` is the mapping of Maven phases to the
plugin goals used to do work during that phase. The table below lists what plugin goals are bound
to which lifecycle phase when the ``majic-cmake`` packaging type is specified.

================= ============= ======================== =======================================
Lifecycle Phase   Plugin        Goal                     What it does
================= ============= ======================== =======================================
generate-sources  majic         cmake-untar-dependencies Unpacks transitive dependencies
process-resources majic         cmake-configure          Runs CMake configuration step
compile           majic         cmake-compile            Runs CMake build target
test              majic         cmake-test               Runs CMake test target
prepare-package   majic         cmake-install            Runs CMake install target
package           majic         cmake-tar                Packages up installed files
clean             majic         cmake-clean              Deletes build files
install           maven-install install                  Installs Artifact locally
deploy            maven-deploy  deploy                   Deploys Artifact to external repository
================= ============= ======================== =======================================

For more information, see the documentation of Majic plugin goals,
found `here </build-helper-maven-plugin/plugin-info.html>`.