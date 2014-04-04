.. _ubuntuInstall:

=================================================
Configuration of new ubuntu 12.04 computer aspn03
=================================================

In this section we show various apt-get installs of packages used for
ASPN development. 

Generally useful packages
=========================

.. index:: p7zip, 7z, emacs

::

   sudo apt-get install \
      emacs23 \
      gnome-system-tools \
      unrar-free \
      p7zip-full \
      synaptic \
      aptitude

:index:`ROS` Desktop
====================

Install ROS following the directions on the `ROS Wiki
<http://www.ros.org/wiki/groovy/Installation/Ubuntu>`__

::

   sudo sh -c \
      'echo "deb http://packages.ros.org/ros/ubuntu precise main" > /etc/apt/sources.list.d/ros-latest.list'
   wget http://packages.ros.org/ros.key -O - | sudo apt-key add -
   sudo apt-get update
   sudo apt-get install ros-groovy-desktop-full


:index:`xerces-c`
=================

::

   sudo apt-get install \
      libxerces-c-dev \
      libxerces-c-doc \
      libxerces-c-samples \
      libxerces-c3.1

:index:`gdal`
=============

The conventional Ubuntu source for gdal does not install with ROS
because it has a conflicting dependency. So I download and build gdal
from source instead. Current source for gdal may be obtained from
`osgeo <http://download.osgeo.org/gdal/CURRENT>`_. Unpack and do an
in-source configure and build with::

   cd gdal-1.10.0
   ./configure --prefix=/home/pmiller/projects/aspn-super-pkg
   make -j12
   make install
   echo export GDAL_ROOT=/home/pmiller/projects/aspn-super-pkg >> ~/.bashrc

If you are not using ROS and want a conventional install of gdal, use::

   sudo apt-get install libgdal1-dev libgdal-doc gdal-bin python-gdal


:index:`Qt4`
============

::

   sudo apt-get install \
      qt4-dev-tools \
      qt4-designer \
      qtcreator \
      qtcreator-doc \
      qt4-doc \
      qt4-demos

:index:`matlab`
~~~~~~~~~~~~~~~

Matlab installers may be obtained from
``\\rebel\it_installs\Matlab\Linux 64-bit``. For the ``R2013a``
version, make a local copy of the entire R2013a folder, unzip the
glnxa64 installer in place, and then run the setup.

::

   cd R2013a
   unzip matlab_R2013a_glnxa64_installer.zip 
   cp matlab_license.dat ~/Documents
   sudo ./install
   echo export PATH=\$PATH:/usr/local/MATLAB/R2013a/bin >> ~/.bashrc

Use the file installation key
``02586-43325-41039-17882-41372-47263``. 

After installation is complete, be sure to add the matlab bin folder
to your :envvar:`PATH` environment variable.
      
:index:`Eclipse` for C++:
=========================

Before installing eclipse, you may want to install an up-to-date JDK
as described below. You will need a JRE, but the one that comes with
Ubuntu should be okay for eclipse. Either way, you may install Eclipse
using::

   sudo apt-get install eclipse-cdt

Then try to run the ``eclipse`` executable. If/when eclipse does not
run because of an error with swt, a `Stack Overflow answer
<http://stackoverflow.com/questions/10970754/cant-open-eclipse-in-ubuntu-12-04-java-lang-unsatisfiedlinkerror-could-not-l>`_
says to::

   ln -s /usr/lib/jni/libswt-* ~/.swt/lib/linux/x86_64/

(The reason you must run eclipse first is the target directory for
this link will not exist until eclipse has been run.)

.. _java install with apt-get:

Oracle Java :index:`JDK` with apt-get
=====================================

PPA installation instructions for the Oracle JDK are found at `webupd8
<http://www.webupd8.org/2012/01/install-oracle-java-jdk-7-in-ubuntu-via.html>`_. To
install::

   sudo add-apt-repository ppa:webupd8team/java
   sudo apt-get update
   sudo apt-get install oracle-java7-installer
   echo export JAVA_HOME=/usr >> ~/.bashrc

The environment variable :envvar:`JAVA_HOME` is a conventional way to
tell java applications where to get the JRE.

:index:`hdf5`
=============

::

   sudo apt-get install libhdf5-serial-dev libhdf5-doc hdf5-tools hdfview

Python Goodies
==============

::

   sudo apt-get install python-setuptools
   sudo easy_install pip
   sudo pip install docutils

The docutils package contains the sphinx documentation builder used to
generate this documentation set.

:index:`Grub`-customizer
========================

To get a gui for customizing grub see `this blog
<https://launchpad.net/~danielrichter2007/+archive/grub-customizer?field.series_filter=precise>`_,
which has the following instructions::

   sudo add-apt-repository ppa:danielrichter2007/grub-customizer
   sudo apt-get update
   sudo apt-get install grub-customizer


Miscellaneous Tips
==================

.. index:: windows, shortcut, snap

Customize Windows Shortcut Keys
-------------------------------

Ubuntu has something very close to the win7 winkey-right/winkey-left
behavior to snap a window to the right or left side of the screen. You
may use Ctrl-Alt 1, 3, 9, or 7 to snap to the bottom left, bottom
right, top right or top left quadrants of the screen. Then you may
maximize vertically, by assigning Super-Up as the windows shortcut
key. To create the short cut key, go to System Settings ->
Hardware/Keyboard -> Shortcuts -> Windows then set the shortcut for
"Maximize window vertically".

.. index:: mount, ntfs

Mount Windows Partitions
------------------------

::

   sudo apt-get install ntfs-config
   sudo ntfs-config

.. index:: dpkg

Use Same Packages as Another Computer
-------------------------------------

To configure a computer with same packages as another computer (from
`askubuntu
<http://askubuntu.com/questions/17823/how-to-list-all-installed-packages)>`__:

* Create a backup of what packages are currently installed::

   sudo dpkg --get-selections > list.txt

* Then (on another system) restore installations from that list::

   sudo dpkg --clear-selections
   sudo dpkg --set-selections < list.txt

* To get rid of stale packages::

   sudo apt-get autoremove

* To get installed like at backup time::

   sudo apt-get dselect-upgrade

