.. _developer-prerequisites:

===================
Setting up a developer computer for Majic
===================

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
      assume you chose C:\\Program Files\\Apache Software Foundation. The
      subdirectory apache-maven-3.1.1 will be created from the archive.

   #. Add the :envvar:`M2_HOME` environment variable by opening up the system
      properties (WinKey + Pause), selecting the "Advanced" tab, and the
      "Environment Variables" button, then adding the M2_HOME variable in
      the user variables with the value C:\\Program Files\\Apache Software
      Foundation\\apache-maven-3.1.1. Be sure to omit any quotation marks
      around the path even if it contains spaces. Note: For Maven 2.0.9,
      also be sure that the M2_HOME doesn't have a '\\' as last character.

   #. In the same dialog, add the :envvar:`M2` environment variable in the user
      variables with the value %M2_HOME%\\bin.

   #. Optional: In the same dialog, add the MAVEN_OPTS environment
      variable in the user variables to specify JVM properties, e.g. the
      value -Xms256m -Xmx512m. This environment variable can be used to
      supply extra options to Maven.

   #. In the same dialog, update/create the :envvar:`PATH` environment variable in
      the user variables and prepend the value %M2% to add Maven
      available in the command line.

   #. In the same dialog, make sure that :envvar:`JAVA_HOME` exists in your user
      variables or in the system variables and it is set to the location
      of your JDK, e.g. :file:`C:\\Program Files\\Java\\jdk{1.5.0_02}` and that
      %JAVA_HOME%\\bin is in your :envvar:`PATH` environment variable.

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


settings.xml
------------

.. index:: settings.xml; installation

You need to tell maven where our packages are installed by creating
``$HOME/.m2/settings.xml`` (where ``$HOME`` is your home directory)
with the following contents

      <?xml version="1.0" encoding="UTF-8"?>
      <settings 
	  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0
			      http://maven.apache.org/xsd/settings-1.1.0.xsd"
	  xmlns="http://maven.apache.org/SETTINGS/1.1.0"
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<profiles>
	  <profile>
	    <id>user-settings</id>

	    <pluginRepositories>
	      <pluginRepository>
		<id>artifactory-vt</id>
		<name>SRI VT Repository</name>
		<url>https://artifactory-vt.sarnoff.internal/artifactory/repo</url>
	      </pluginRepository>
	    </pluginRepositories>

	    <repositories>
	      <repository>
		<id>artifactory-vt</id>
		<name>SRI VT Repository</name>
		<url>https://artifactory-vt.sarnoff.internal/artifactory/repo</url>
	      </repository>
	    </repositories>

	    <properties>
	    </properties>
	  </profile>
	</profiles>

	<activeProfiles>
	  <activeProfile>user-settings</activeProfile>
	</activeProfiles>
      </settings>

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
