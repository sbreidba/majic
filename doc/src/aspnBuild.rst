=====================
 Building aspn-super
=====================

Configure the computer following the instructions given in
:ref:`Ubuntu Install <ubuntuInstall>` notes.

Install Project Specific Packages
=================================

Install the following project specific packages:

:index:`boost` 1.48
-------------------

::

   cd ~/Downloads
   wget http://artifactory-vt/artifactory/simple/vt-libraries/com/sri/vt/aspn/boost/1.48/boost-1.48-ubuntu_x86_64.z7
   mkdir ~/projects/aspn-super-pkg
   cd ~/projects/aspn-super-pkg
   7z x ~/Downloads/boost-1.48-ubuntu_x86_64.z7
   cd boost-1.48
   echo export BOOST_ROOT=`pwd` >> ~/.bashrc

The simplest way to make sure this is the version of boost found by
cmake is to set the :envvar:`BOOST_ROOT` as shown above.

.. index:: rdm

:index:`DEMData`
----------------

::

  cd ~/Downloads
  wget http://artifactory-vt/artifactory/simple/vt-libraries/com/sri/vt/aspn/DEMData/1.0/DEMData-1.0-data.z7
  sudo mkdir /opt/sarnoff
  cd /opt/sarnoff
  7z x ~/Downloads/DEMData-1.0-data.z7 

Fetch and Build
===============

.. todo::

   Need to update and simplify these instructions for building.


The simplest build of this meta project for aspn may be obtained using
the following::

   $ cmake -P fetch.cmake
   $ mkdir ../aspn-super-build
   $ cd ../aspn-super-build
   $ cmake -DPKG_PATH=../aspn-pkg-vc10 -DINSTALL_PATH=exports -P ../aspn-super/build.cmake

Possible variations include:

- when getting the source code from git-open, you may need to use
  ``-Dgit-open=git-open`` (without the default .sarnoff.com domain
  suffix) if your ssh key was generated without the ``.sarnoff.com``
  domain suffix. (Mine, Phil's, is, but the default seems to be okay
  for most people.)

- fetch from the externally accessible ``aspn@aspn-sri.sarnoff.com``
  using::

  $ cmake -Dgit-open=aspn-sri.sarnoff.com -Dgit-open-user=aspn@ -P fetch.cmake 

.. index:: geographiclib

The above usage of the build.cmake script assumes that you have
already pre-built and installed the external packages required to
``../aspn-pkg-vc10``. When you are building on Ubuntu 12.04, the only
package not easily obtained from the Software Center or apt-get is
`GeographicLib from sourceforge
<http://sourceforge.net/projects/geographiclib>`_.

I built geographic lib using::

   cmake -G "Visual Studio 10 Win64" \
      -DINSTALL_PATH=__PATH_TO_YOUR_EXPORTS_GOES_HERE__ \
      -DGEOGRAPHICLIB_DOCUMENTATION=ON \
      -DCOMMON_INSTALL_PATH=ON \
      -DMATLAB_COMPILER=mex \
      c:\Users/pmiller/projects/aspn-super-build-pkg/extproj/Source/geographiclib
