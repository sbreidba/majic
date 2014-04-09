============
Introduction
============

MAJIC - Maven, Artifactory and Jenkins Integrated with Cmake

Majic is Maven plugin built to work with CMake. The plugin can
build multi-platform projects created for CMake then
apply Artifactory+Jenkins style dependency management 
to the resultant binary artifacts. Majic also leverages the
`convention over configuration <http://en.wikipedia.org/wiki/Convention_over_configuration>`_
design philosophy 
of Maven and applies it to CMake --- and in particular to CMake's
package finding system --- to simplify the CMake build environment
and reduce complexity.

Jenkins and Artifactory allow Majic to rapidly scale to 
manage hundreds of builds and dependencies, on multiple platforms,
without the project developers having to manually build each dependency
themselves. Instead, a Jenkins continuous integration build automatically 
builds a project after the source code is changed, then cascades to build all projects
that are dependent upon the changed project. The resultant binaries
are published on Artifactory and made available to developers.

Majic also reduces or eliminates the need for
software packages to be installed on build servers and developer computers by
applying the convention over configuration philosophy to CMake dependencies. 
Software packages from third parties are packaged in a standardized format and 
uploaded to Artifactory. The packages are then automatically downloaded
to a developer's computer, via Maven dependency management, and made
available to CMake's package finding infrastructure.
This can virtually eliminate the need for installing software on developer
computers, with exceptions such as projects requiring special licensing.

The end result is that a developer needs only to download a given project
from source control and invoke Maven to build it. Maven
will automatically fetch all the latest dependencies and make them 
available to the project being developed. The developer no longer
has to build dependencies or install prerequisite packages to work
on a project. This makes bringing on new developers vastly easier, as is
configuring a build server. The convention over configuration 
design philosophy reduces the complexity of the overall system.

Majic currently supports CMake with C++ for Windows Visual Studio 2010
and 2012, as well as gcc on Linux. However, Majic is not limited 
to these platforms, nor is it limited to CMake. Majic can be scripted
to use other build systems such as nmake and ant.

Getting Started as a Developer
===============================

1. Install the :ref:`required components <developer-prerequisites>` to your local computer. 
2. Clone the desired repository that is Majic ready to your local computer.
3. Open a standard command line prompt in the cloned directory. On Windows, do NOT use a visual studio command line prompt.
4. run "mvn install" 

Common Command Line Options
===============================

- **cmake.arch** The default Majic architecture is 64 bit. To specify another architecture 
  use "-Dcmake.arch=[value]". Valid values are "32" and "64".

- **cmake.compiler** The default compiler is Visual Studio on Windows. If more than
  one version of Visual Studio is installed, the most recent is
  used. To specity a compiler use "-Dcmake.compiler=[value]".
  Valid values are "vc2010" and "vc2012". The default compiler on linux is gcc. 
  It is the only supported compiler.