.. _configuring-jenkins-jobs:

===================
Working with Jenkins
===================

Creating a new Majic Build
===============

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
  
