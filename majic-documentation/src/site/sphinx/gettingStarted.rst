============
Introduction
============

MAJIC - Maven, Artifactory and JenkIns with Cmake

Majic is Maven plugin built to work with CMake. The plugin can
build multi-platform projects created for CMake then
apply Maven/Artifactory/Jenkins style dependency management 
to the resultant binary artifacts.

Jenkins allows Majic to rapidily scale to 
manage hundreds of builds and dependencies, on multiple platforms,
without the project developers having to manually build each dependency
themselves. Instead, a Jenkins continuous integration build automatically 
builds a project after the source code is changed, then cascades to build all projects
that are dependant upon the changed project. The resultant binaries
are published on Artifactory and made available to developers.

An additional goal of Majic is to reduce or eliminate the need for
software packages to be installed on build servers and developer computers. Instead,
software packages are uploaded to Artifactory and automatically downloaded
to a developer's computer via Maven dependency management. Majic makes 
such dependencies available to CMake's package finding infrastructure.
This virtually eliminates the need for installing software on developer
computers, with a exceptions such as projects requiring special licensing.

The end result is that a developer needs only to download a given project
from source control and invoke Maven to build it. Maven
will automatically fetch all the latest dependencies and make them 
available to the project being developed. The developer no longer
has to build dependencies or install prerequisite packages to work
on a project. This makes bringing on new developers vastly easier, as is
configuring a build server.

Majic currently supports CMake with C++ for Windows Visual Studio 2010
and 2012, as well as gcc on Linux. However, Majic is not limited 
to these platforms, nor is it limitted to CMake. Majic can be scripted
to use other build systems such as nmake and ant.

Getting Started as a Developer
===============================

1. Install the required components to your local computer. :ref:`developer-prerequisites`
2. Clone the desired repository that is Majic ready to your local computer.
3. Open a standard command line prompt in the cloned directory. On Windows, do NOT use a visual-studio command line prompt.
4. "mvn install" 

Common Command Line Options
===============================

- *cmake.arch* The default Majic architecture is 64 bit. To specify another architecture 
  use "-Dcmake.arch=[value]". Valid values are "32" and "64".

- *cmake.compiler* The default compiler is Visual Studio on Windows. If more than
  one version of Visual Studio is installed, the most recent is
  used. To specity a compiler use "-Dcmake.compiler=[value]".
  Valid values are "vc2010" and "vc2012". The default compiler on linux is gcc. 
  It is the only supported compiler.

A note about this documentation
===============================

This documentation is written in RestructuredText and generated
using Sphinx. This is a common toolset, particularly in the python
domain, but is also widely used elsewhere; e.g., The OpenCV
documentation uses this toolset. If you are unfamilar with
RestructuredText and Sphinx, here are a few links to get you
started:

- `Installation
  <http://docutils.sourceforge.net/README.html#installation>`__

- `ReStructuredText Primer <http://sphinx-doc.org/rest.html>`__
  describes the basics of ReStructuredText markup.

- `Sphinx Markup <http://sphinx-doc.org/markup/index.html>`__
  describes relevant additional constructs available when using the
  sphinx builder.

The above links should be adequate for anyone wishing to contribute to
this documentation. 
