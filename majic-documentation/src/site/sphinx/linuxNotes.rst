
.. only:: html

   .. contents::
      :depth: 3
      :backlinks: top

============================
Reference: Miscellaneous Notes on Linux
============================

gcc
===

predefined macros
-----------------

.. index:: gcc; predefined macros

`This post
<http://stackoverflow.com/questions/1936719/what-are-the-gcc-predefined-macros-for-the-compilers-version-number>`__
on stackoverflow provides a very handy tip for getting
:command:`gcc` to give you all of the predefined macros that is uses.

:: 

   gcc -E -dM - < /dev/null

Version Macros
--------------

__GNUC__
   major version number

__GNUC_MINOR__
   minor version number

__GNUC_PATCHLEVEL__
   patch level number

For example, Ubuntu 12.04 comes with gcc 4.6.3 and Centos6 comes with
gcc 4.4.7. To distinguish between the two versions, you may use::

   __GNUC__ == 4 && __GNUC_MINOR__ < 5

when you encounter differences between the newer gcc with Ubuntu and
the older gcc with Centos6.

Add Disk to VM
==============

Use the virtual machine software application (either VMWare
Workstation or VDirector) to add a new disk to the VM. Then I followed
this posting, http://blog.jiwen.info/?p=115, which mostly consists of 

#. Create the partition using ``fdisk``. For my system I used ``fdisk
   /dev/sdb`` since it was my second drive. Ater using ``m`` for help
   (i.e., menu), I believe I used the commands ``n`` for new, and
   ``p`` for parition, and ``w`` for writing.

#. Format the filesystem using ``mkfs``. I used ``mkfs -t ext3
   /dev/sdb1``

#. Create a mount point: ``mkdir /disk2``

#. Add this line to /etc/fstab::

      /dev/sdb1   /disk2   ext3   default 1 2  

#. reboot or just remount using ``mount -a``

IPP and MKL
===========

Get a license, download the latest version, and install following the
instructions. You will get a warning about installing on an
unsupported operating system, but you may ignore that because it seems
to work on CentOs 5.7, 5.8, 6.3, and OpenSuse 12.2.


Install :index:`Matlab <matlab>`
================================

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


VPN
===

.. index:: vpn, snx, whitebeard

.. todo::

   These instructions for vpn are now out of date. Need to bug Doug
   Corliss to get a new set of instructions.

Use `this link <https://whitebeard.sarnoff.com>`_ and download and
install the snx application. It will likely not run until you also
install the following dependent packages::

   sudo apt-get install libstdc++5:i386
   sudo apt-get install libpam-dev:i386

Then you may run it with your SRI certificate using::

   snx -g -s whitebeard.sarnoff.com -c /home/pmiller/Documents/pmiller.p12
