==========================================================
Configuring a Linux (CentOs/RedHat) System for Development
==========================================================

EPEL
====

  $ wget http://download.fedoraproject.org/pub/epel/5/i386/epel-release-5-4.noarch.rpm
  $ rpm -Uvh epel-release-5-4.noarch.rpm

Or simply click on this link: http://download.fedoraproject.org/pub/epel/5/i386/epel-release-5-4.noarch.rpm .

RepoForge
=========

`RepoForge <http://repoforge.org/use/>`__ is the next generation
rpmforge. RepoForge was the only place I could find a reasonably up to
date git. The system install of git was 1.7.1, but when I used the
*annotate* feature in the new qtcreator, it complained that it needed
at least git version 1.7.2. So I added the RepoForge `EL 6 x86_64
<http://pkgs.repoforge.org/rpmforge-release/rpmforge-release-0.5.3-1.el6.rf.x86_64.rpm>`__
repository and got 1.7.11.3.

boost
=====

Before building install::

   sudo yum install bzip2-devel
   sudo yum install zlib-devel

Build using::
 
   ./bootstrap.sh --prefix=$HOME/projects/aspn-pkg
   ./b2 --build-type=complete --layout=tagged -j4 address-model=64 link=static,shared install

.. todo:: 

   installation/building of boost become part of the general
   documentation of clavin and should not be mentioned in this CentOs
   section. 

7zip
====

Using the epel repository::

   sudo yum install p7zip-plugins

xerces-c-dev
============

Install using::

   sudo yum install xerces-c-devel

gdal
====

There are various versions of gdal available from several
repositories. At the current time (October 14, 2013), the choices are: 

EPEL
   1.7.3 (Nov, 2010)

`EL GIS <http://elgis.argeo.org/repos/6/elgis-release-6-6_0.noarch.rpm>`__
   1.9.2 (Oct, 2012), 1.9.1, 1.8.1, 1.8.0, 1.7.3, and 1.7.2

`osgeo <http://download.osgeo.org/gdal>`__ source distribution:
   1.10.1 (Aug, 2013)

doxygen (and graphviz)
======================

The installed doxygen (1.6) is old and does not meet the requirements
for generating geographiclib's docuementation. The easiest thing is to
uninstall doxygen and install it locally. You may get the source from:
`here <http://www.stack.nl/~dimitri/doxygen/download.html>`__. Then
use the following commands::

   sudo yum remove doxygen
   sudo yum install graphviz

   cd ~/projects/pkg-src
   zcat ~/Downloads/doxygen-1.8.5.src.tar.gz | tar -xvf -
   cd doxygen-1.8.5
   ./configure
   make -j6 -l6
   sudo make install
   

Packages Peculiar to Locktight
==============================

ACE
---

Copy the repo file from
http://download.opensuse.org/repositories/devel:/libraries:/ACE:/bugfixonly/CentOS_CentOS-5/devel:libraries:ACE:bugfixonly.repo
to :file:`/etc/yum.repos.d/OpenSUSE-ACE.repo`


Add/Remove Software
-------------------

From Add/Remove Software, Development, make sure the following are checked:

#. Development Libraries

   #. Development Tools
   #. Legacy Software Development
   #. X Software Development

   After that is done, remove boost 1.33.

#. ace-devel
#. boost-devel
#. gdal-devel
#. gsoap-devel
#. openmotif-devel
#. xerces-c-devel

(See note below on configuring boost on CentOs 5.7.)

Qt
--

Qt is not readily available for Centos 5.7. On Centos 6.3, you can get
Qt from the base repository, but it is old. On windows I could not
find a 64-bit distribution for VS 2010. So we now have our own Qt
source tree on git-open that is a clone of the one on gitorious. Here
are the build instructions for Linux:

.. code-block:: bash

   $ git clone ssh://<username>@git-open/scm/vendor/gitorious/qt.git
   $ cd qt
   $ ./configure

   $ make -j8
   $ make install

   # default configuration of Qt will be install into /usr/local/Trolltech/Qt-4.8.5
   #
   # the cmake find scripts for Qt use the ``qmake`` that it finds
   # first in your path when setting paths to Qt components, so make a
   # softlink to qmake to put it early in your path.

   $ ln -s /usr/local/Trolltech/Qt-4.8.5/bin/qmake \
           someDirectoryAtTheBeginningOfYourPath

Red Hat Software Collections for new gcc and friends
====================================================

First install the scl package::

   sudo yum install scl-utils scl-utils-build

Then install the collection itself (this is what includes a new gcc
and is described at http://qt-project.org/wiki/Building_Qt_5_from_Git:: 

   sudo wget http://people.centos.org/tru/devtools-1.1/devtools-1.1.repo -O /etc/yum.repos.d/devtools-1.1.repo
   sudo yum install devtoolset-1.1

Enable the software collection::

    scl enable devtoolset-1.1 bash
     
    # Test - Expect to see gcc version 4.7.2 ( * not * gcc version 4.4.7 )
    gcc -v

Build qt5 from git
==================

    # Install missing Qt build dependencies:
    yum install libxcb libxcb-devel xcb-util xcb-util-devel
     
    # Install Red Hat DevTools 1.1 for CentOS-5/6 x86_64
    wget http://people.centos.org/tru/devtools-1.1/devtools-1.1.repo -O /etc/yum.repos.d/devtools-1.1.repo
    yum install devtoolset-1.1
     
    # Open new terminal in ~/projects folder and enable devtoolset-1.1
    mkdir ~/projects
    cd ~/projects
    scl enable devtoolset-1.1 bash
     
    # Test - Expect to see gcc version 4.7.2 ( * not * gcc version 4.4.7 )
    gcc -v
     
    # Git Qt source
    git clone git://gitorious.org/qt/qt5.git qt5
    cd qt5
    git checkout stable
    perl init-repository
     
    # Clean and configure
    # Optional clean is needed if re-configuring
    git submodule foreach --recursive "git clean -dfx"
    ./configure -opensource -nomake examples -nomake tests -no-gtkstyle -confirm-license -qt-libpng -no-c++11
     
    # If making on multi-core, for example a quad-core,use "make -j 4"
    make
     
    # make install copies to /usr/local/Qt-5.1.2/
    # Run as su or using sudo
    make install
     
    # Build Qt Creator
    export QTDIR=/usr/local/Qt-5.1.2/
     
    # Git Qt Creator source
    cd ~/projects
    git clone git://gitorious.org/qt-creator/qt-creator.git
    cd qt-creator
     
    ${QTDIR}/bin/qmake -r
    make
     
     ./bin/qtcreator &

