==============
Working with Artifactory
==============

Prebuilt Packages
=====

Majic can manage projects from third parties as dependencies for Majic projects. 

There are two ways to incorporate a third party library:

- Setup up a source control repository and a Majic build process for the library
- Build the third party project manually (on a developer computer) and upload the 
  resulting package to artifactory. Such artifacts are referred to as "prebuilt" artifacts.

Deploying a Prebuilt Package
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


