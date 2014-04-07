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
software packages to be installed on developer computers. Instead,
software packages are uploaded to Artifactory and automatically downloaded
to a developer's computer via Maven dependency management. Majic makes 
such dependencies available to CMake's package finding infrastructure.
This virtually eliminates the need for installing software on developer
computers, with a exceptions such as projects requiring special licensing.

The end result is that a developer needs only to download a given project
from source control and invoke Maven to build it. Maven
will automatically featch all the latest dependencies and make them 
available to the project being developed. The developer no longer
has to build dependencies or install prerequisite packages to work
on a project.

Majic currently supports CMake with C++ for Windows Visual Studio 2010
and 2012, as well as gcc on Linux. However, Majic is not limited 
to these platforms, nor is it limitted to CMake. Majic can be scripted
to use other build systems such as nmake and ant.

Developing a Majic-Based Project
================================

Artifactory is a server-based product with the VT instance running at
https://artifactory-vt.sarnoff.internal.  Please make sure you have read access to this
server.

Typical developers do not install jenkins on their individual
development computers, so we discuss that later in this document in
:ref:`configuring-jenkins`.

The rest of this section will focus
on the installation of CMake and Maven.

Maven Installation
==================

Prerequisite Java JDK
---------------------

.. index:: jdk; installation

Download the jdk from `oracle.com
<http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html>`__.
At the time this was written, October, 2013, the latest version was
7u45.

windows
   Download and install the Windows x64
   :file:`jdk-{7u45}-windows-x64.exe`

CentOs
   Download and install the file
   :file:`jdk-{7u45}-linux-x64.rpm`. Then install using::
   
      sudo rpm -Uvh $HOME/Downloads/jdk-7u40-linux-x64.rpm

   I found the above rpm command `here
   <http://www.if-not-true-then-false.com/2010/install-sun-oracle-java-jdk-jre-7-on-fedora-centos-red-hat-rhel/>`__

Ubuntu
   Download and unpack the file
   :file:`jdk-{7u45}-linux-x64.tar.gz`. You will need to set the
   :envvar:`JAVA_HOME` to point to the parent folder of
   :file:`bin/java`

   Alternatively, :ref:`java install with apt-get` shows how to use
   :command:`apt-get` to install the jdk. Installing with apt-get will
   provide automatic notifications of updates.

Note that Maven really only requires the Java *jdk*; the *jre* is sufficient
unless you are actually compiling Java code.

Maven Installation Instructions
-------------------------------

.. index:: maven; installation

The content in this subsection was copied from maven 
`Installation Instructions <http://maven.apache.org/download.cgi>`__.

Windows
   #. Unzip the distribution archive, i.e. apache-maven-3.1.1-bin.zip to
      the directory you wish to install Maven 3.1.1. These instructions
      assume you chose C:\Program Files\Apache Software Foundation. The
      subdirectory apache-maven-3.1.1 will be created from the archive.

   #. Add the :envvar:`M2_HOME` environment variable by opening up the system
      properties (WinKey + Pause), selecting the "Advanced" tab, and the
      "Environment Variables" button, then adding the M2_HOME variable in
      the user variables with the value C:\Program Files\Apache Software
      Foundation\apache-maven-3.1.1. Be sure to omit any quotation marks
      around the path even if it contains spaces. Note: For Maven 2.0.9,
      also be sure that the M2_HOME doesn't have a '\' as last character.

   #. In the same dialog, add the :envvar:`M2` environment variable in the user
      variables with the value %M2_HOME%\bin.

   #. Optional: In the same dialog, add the MAVEN_OPTS environment
      variable in the user variables to specify JVM properties, e.g. the
      value -Xms256m -Xmx512m. This environment variable can be used to
      supply extra options to Maven.

   #. In the same dialog, update/create the :envvar:`PATH` environment variable in
      the user variables and prepend the value %M2% to add Maven
      available in the command line.

   #. In the same dialog, make sure that :envvar:`JAVA_HOME` exists in your user
      variables or in the system variables and it is set to the location
      of your JDK, e.g. :file:`C:\Program Files\Java\jdk{1.5.0_02}` and that
      %JAVA_HOME%\bin is in your :envvar:`PATH` environment variable.

   #. Open a new command prompt (Winkey + R then type cmd) and run 
      ``mvn --version``  to verify that it is correctly installed.

Unix-based Operating Systems (Linux, Solaris and Mac OS X)
   #. Extract the distribution archive,
      i.e. apache-maven-3.1.1-bin.tar.gz to the directory you wish to
      install Maven 3.1.1. These instructions assume you chose
      /usr/local/apache-maven. The subdirectory apache-maven-3.1.1 will
      be created from the archive.

   #. In a command terminal, add the :envvar:`M2_HOME` environment variable,
      e.g. ``export M2_HOME=/usr/local/apache-maven/apache-maven-3.1.1``.

   #. Add the :envvar:`M2` environment variable, e.g. ``export M2=$M2_HOME/bin``.

   #. Optional: Add the MAVEN_OPTS environment variable to specify JVM
      properties, e.g. export MAVEN_OPTS="-Xms256m -Xmx512m". This
      environment variable can be used to supply extra options to Maven.

   #. Add M2 environment variable to your path, e.g. 
      ``export PATH=$M2:$PATH``.

   #. Make sure that :envvar:`JAVA_HOME` is set to the location of your JDK,
      e.g. ``export JAVA_HOME=/usr/java/jdk1.5.0_02`` and that ``$JAVA_HOME/bin``
      is in your PATH environment variable.

   #. Run ``mvn --version`` to verify that it is correctly installed.


cmake Installation
==================

Linux
   Use the source distribution in our local cmake repository on
   git-open. The commands below will install :command:`cmake` to
   :file:`/usr/local`:: 

      git clone ssh://git-open/scm/3rdparty/cmake.git -b v2.8.12
      mkdir cmake-build
      cd cmake-build
      ../cmake/configure
      make -j4 -l4
      sudo make install

Windows
   Use the latest installer from `cmake.org
   <http://www.cmake.org/cmake/resources/software.html>`__. 

Running Maven with Majic and common options
===============================

- Start a build by running "mvn install" in the folder containing pom.xml.
  On windows, the build should be run from a standard command prompt,
  NOT from a Visual Studio command prompt. Also, do not setup the vcvars
  environment in the standard command prompt.

- The default architecture is 64 bit. To specify an architecture 
  specify an architecture use -Dcmake.arch=[value]. Valid values
  are "32" and "64".

- The default compiler is Visual Studio on Windows. If more than
  one version of Visual Studio is installed, the most recent is
  used. To specity a compiler use -Dcmake.compiler=[value].
  Valid values are "vc2010" and "vc2012".

- The default compiler on linux is gcc. It is the only supported
  compiler.


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
