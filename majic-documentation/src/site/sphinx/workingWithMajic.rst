==============
Working with Majic
==============

Overview
=====

Majic projects are built with Maven. To build the project run "mvn install" from a command line in the project's folder.

Majic projects use a standard folder layout: ::

  <parentfolder>            an aribitrary parent folder for the project
    <project>               the project's root folder. This has the pom.xml file and usually CMakeLists.txt
      cmake                 the project's cmake configuration files
      src                   the projects source code (this is an optional location. many projects differ)
        <project>-build	    the default output folder for all Majic builds
          <classifier>	    the output folder for a build for specific platform. 
                            ex. "win7-vc2010-32", "centos-gcc-64"
            imports         the project's dependencies are unpacked here. CMake's find_package uses these.
            <project>	    the project's working area for the build
              exports
                <project>-<version> contains a copy of the project's public binaries, includes, libs, cmake  
                                    configurations and any other files that this project will make available 
                                    to others.

Creating a new Majic build on Jenkins
===================

Majic uses two Jenkins jobs to build a single Majic project. The "master" job monitors source control for changes and
launches the "matrix" job to perform the build(s) for the specific platforms and configurations, such as 32-bit Windows 7
with Visual Studio 2010, or 64-bit Centos with GCC. The master waits for the matrix jobs to complete, then packages
up the artifacts from the matrix jobs into a single master artifact.

To create a new set of Majic jobs:

Log in to the server, such as http://git-open.sarnoff.internal

1.	Create the matrix job. Click "New Job" in the UI. 

  a.	Copy existing Job: 000-vt-template-matrix; call it "<my project>-matrix". Ex: "foo-matrix". This naming convention is required.
  b.	Uncheck "Disable build"
  c.	Edit the "repository url" for your branch. Do not edit the "branch specifier"
  d.	Check or uncheck the slaves to run on in the NODE_LABEL axis (centos6 / vc2010 / vc2012).
  e.	Set "User Defined Axis" named "ARCH" as needed. Remove platforms not needed (ex. remove "32")
  f.	Click "Save"
2.	Create the master job. Click "New Job" in the UI. 

  a.	Copy existing Job: 000-vt-template; call it "<my project>". Ex: "foo". This naming convention is required.
  b.	Uncheck "Disable build"
  c.	Edit the "repository url" for your branch AND set the correct "branch specifier"
  d.	Click "Save"
  
Prebuilt Packages
=====

Majic can manage projects from third parties as dependencies for Majic projects. 

There are two ways to incorporate a third party library:

- Setup up a source control repository and a Majic build process for the library
- Build the third party project manually (on a developer computer) and upload the 
  resulting package to artifactory. Such artifacts are referred to as "prebuilt" artifacts.

Deploying a Prebuilt Package to Artifactory
=====

- Build the project locally. Be sure to produce both debug and release versions.
- Create a folder structure to hold the prebuilt library. Typically the top level
  is the name of the project. Subdirectories include:

  - **bin** dynamic libraries and associated binaries. Both debug and release.
  - **include** header files
  - **lib** C/C++ link libraries (.lib) and associated binaries. Both debug and release.

- Add a <project-name>-config.cmake CMake script to the project if it doesn't already have one.
- From the parent of the folder structure you created, create a bzip2 tarball of the project with the following command: 

  - **tar jcvf <project-name>.tar.bz2 <project-name>**
  - On Windows, cmake provides tar if you need it. "cmake -E tar jcvf <project-name>.tar.bz2 <project-name>"

- Upload the tarball to Artifactory:

  - Log in to Artifactory
  - Select the "Deploy" tab, choose your file, and select "Upload!".
  - At the "Artifact Deployer" screen, set the following information:

    - groupid: typically "com.sri.vt.3rdparty"
    - artifactid: typically a lower case project name
    - version: typically the project version followed by "-PREBUILT", optionally "-SNAPSHOT", and finally ".tar"
      ex. "1.5.1-PREBUILT-SNAPSHOT"
    - classifier: specifies the platform(s) supported by the artifact. Typical values:

      - "win7-vc2010-32.tar"
      - "win7-vc2012-64.tar"
      - "centos6-gcc-64.tar"

    - type: "bz2"
  - Select "Deploy Artifact"

Deploying Release Builds
=========================

During the development cycle it's likely that many project artifacts will use a version 
string which contains the word "SNAPSHOT". SNAPSHOT is a special keyword for Maven and 
Artifactory used for development builds. Snapshots make dependency management easier 
but when it's time to release a project the snapshot version cannot be used. All artifacts 
must be "release" builds, ie. they don't have "SNAPSHOT" in the version string. 
This document outline the process of converting a project and all dependencies to release builds. 
For more details on SNAPSHOT builds vs. release builds, `see <http://www.tutorialspoint.com/maven/maven_snapshots.htm>`_ 
`these <http://stackoverflow.com/questions/5901378/what-exactly-is-a-maven-snapshot-and-why-do-we-need-it>`_ 
`links. <http://maven.apache.org/guides/getting-started>`_

To create a release build perform the following steps:

1.	Get a list of what needs to be done for the project being released. Run "mvn dependency:tree -Dverbose" to obtain a list of dependencies. Any dependencies with "SNAPSHOT" in the version string must be converted. 
2.	Start with the "leaf" projects --- projects that don't have dependencies on other SNAPSHOT artifacts --- then work your way through the list.
3.	Once all dependencies have been converted the project being released must also be converted.
4.	Once the release version of the project has been successfully built and labelled as per below, increment the version number and change it back to a SNAPSHOT build to continue development on the next version of the project.
5.	Use the conversion processes detailed below.

Converting Snapshots to Release builds
====================


1.	Tell anyone who might care that they should hold off on commits to the project. 

	a.	If you are using git flow run "git flow release start"

2.	Update the majic parent version if the parent version is a snapshot. Run "mvn versions:update-parent"
3.	Look for dependency updates (if this is a leaf, obviously there won't be any). All dependencies must be updated to release versions.

	a.	To get a list of available released dependencies, run "mvn versions:display-dependency-updates"
	b.	*and/or* run "mvn versions:use-latest-releases". This will update the pom file to use the most recent release of all dependencies. It also creates a backup of the original pom file which can be deleted.
	c.	Take a look at the diff for the pom file --- make sure it's sensible. It should only be list non-snapshot versions as dependencies.
4.	Update the project's version number

	a.	Run "mvn versions:set" to update the pom file. This will walk you through the process of updating the version string. Be sure to remove the keyword "SNAPSHOT" from the string.
	b.	*or* you can edit the pom file manually, but if you are using a multi-module/aggregator project (like Majic itself, or Forgersnoop) then you have to remember to update multiple sub-pom files, while "mvn versions:set" will prompt you for each module's version. 
5.	Run "mvn install" to build the project to make sure it still builds
6.	Commit the changes to source control and wait for a successful build on Jenkins. 
7.	Create a tag or label in source control once the Jenkins build succeeds. (Don't tag in advance in case the build is bad - you can't move git tags. Also - use "annotated" tags only in git.) 

	a.	If you are using git flow run "git flow release finish"

Converting release builds to a snapshot
==================================

After a release build has been created a project is typically converted back to a SNAPSHOT build to continue development on the next version.

1.	Run "mvn versions:set" to update the version. Make sure to increment the version number and use the keyword "SNAPSHOT" in the version string.

	a.	"mvn versions:display-dependency-updates -DallowSnapshots" is useful for seeing what dependencies are available.
2.	Commit to source control. The usual comment is "Prepare for development."


