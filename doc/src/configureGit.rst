.. only:: html

   .. contents::

.. _Configuring git:

===========
git and ssh
===========

Installing git
==============

:linux: 

   On linux installing :command:`git` is most easily done using ``sudo
   apt-get install git`` or ``sudo yum install git`` for either Ubuntu
   or CentOS.
   
:windows:

   On windows `Git Extensions
   <http://sourceforge.net/projects/gitextensions/files/latest/download>`_
   is a popular packaging of git. Git Extensions comes with a
   :command:`bash` command interpreter and all of the required
   :command:`putty` utilities.

:smartgit gui client:

   I cannot effectively use git via command line only. I have to have
   a gui client. I use `smartgit
   <http://www.syntevo.com/smartgithg/download>`_, which has nominal
   (about $80) license fee but is portable across and linux and may be
   installed on ALL the computers you use.

Configuring ssh
===============

Before you can use git, you must also have your :index:`ssh keys` set
up. Charles says you are supposed to create a new private key for
every computer account. He has very good instructions on the `Git-ssh
<https://wiki.sri.com/display/VT/Git-ssh>`__ page on the `VT Wiki
<https://wiki.sri.com/display/VT/Home>`__. I think it is easier and
better to reuse mine, so I copy them from an existing :file:`~/.ssh`
folder. I did create a second private key, ``pkey2`` that I am using
for bitbucket.  After copying the keys to the new account, be sure to
remove insecure permissions to it by::

   $ chmod -R g-rwx,o-rwx ~/.ssh

For other accounts to be able to :command:`ssh` to this computer
without using a password, you need to add your public key(s) to the
:file:`~/.ssh/authorized_keys` file.

You should also copy and install your SRI Certificate so you can
access the `SRI Insider <https://insider.sri.com>`_.

Tips
====

.. toctree::
   :maxdepth: 2

   usingGitSvn
   gitTips
