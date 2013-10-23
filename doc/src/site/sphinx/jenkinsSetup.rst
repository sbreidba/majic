.. _configuring jenkins:

===================
Configuring Jenkins
===================

Install Jenkins
===============

The documentation for Jenkins says that to use stable releases use::

   wget -q -O - http://pkg.jenkins-ci.org/debian-stable/jenkins-ci.org.key | sudo apt-key add -
   sudo sh -c 'echo deb http://pkg.jenkins-ci.org/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
   sudo apt-get update
   sudo apt-get install jenkins

To use more recent releases remove the ``-stable`` suffix from
``debian-stable`` in the two http URLs above.

Configure ssh Keys for the jenkins User
=======================================

There is a lot of unexplained/misremembered folklore relating to
proper configuration of a jenkins build account. Here is what I did
for the aspn build.

I first logged into the jenkins account on the computer running the
jenkins server using the following command as explained in this `Stack
Overflow answer
<http://stackoverflow.com/questions/15314760/managing-ssh-keys-within-jenkins-for-git>`_

.. code-block:: bash

  # Login as the jenkins user and specify shell explicity,
  # since the default shell is /bin/false for most
  # jenkins installations.
  sudo su jenkins -s /bin/bash

This gave me a command prompt for the jenkins user account. Then I
generated an empty passphrase key using::

   ssh-keygen

From another terminal session I ssh'ed to ``vtbuild-open@git-open`` using
the credentials for the vtbuild-open account. Then I vi'ed the
:file:`~/.ssh/authorized_keys` file, and at the very end of that file, I
pasted the contents from the public key from the
:file:`~jenkins/.ssh/id_rsa.pub`. To verify that it works, use::

  ssh vtbuild-open@git-open date

When you are able to get this to execute with no errors *AND* no
prompts, the jenkins build should be able to use the vtbuild-open
account for cloning git repositories. In addition to setting up the
ssh keys, you need to configure git for the jenkins account using::

   git config --global user.email jenkins@aspn
   git config --global user.name "Jenkins Build"

Verify that git works without prompts using::

   git clone ssh://vtbuild-open@git-open/scm/vision/vtcmake.git

.. tip:: 

   Other repositories and build accounts
      Above I am describing how you get the jenkins public key into the
      ``vtbuild-open`` account on the ``git-open`` server. You will need
      to repeat these steps for each additional git server and user that
      you want Jenkins to use. For aspn, you must repeat this for the
      shared ``aspn-user`` account on ``aspn-sri.sarnoff.com``
      server. The aspn account is used to clone the gtsam repository from
      this externally accessible server. For other projects you may also
      need to repeat it for ``vtbuild-itar`` account on the ``git-itar``
      server.

Configure vtbuild-open user account on a slave computer
=======================================================

For jenkins to execute jobs on a slave, the master jenkins account
needs ssh privileges to the slave. Following the current VT
conventions for Jenkins, this is done by creating a user named
``vtbuild-open`` on the slave.

After creating the ``vtbuild-open`` account (I am sure there is a
command line way that Doug or Skip would use, but I use the gui for
this), put the public key for the jenkins user on the master into the
newly created vtbuild-open :file:`~/.ssh/authorized_keys` file
following the same instructions as above. (Remember: put the contents
of the :file:`~.ssh/id_rsa.pub` from the jenkins user account on the
master into the :file:`vtbuild-open:.ssh/authorized_keys` file on the
slave.)

Then repeat these steps so that the vtbuild-open account on the slave
can clone from git-open without prompts. In summary there are two
required special :command:`ssh` permissions that are needed
for a Jenkins master/slave relationship. The two verification steps are:

1.  in a terminal window for the *jenkins* account on the
    *vtbuild-open* computer execute::

       jenkins@vtbuild-open$ ssh vtbuild-open@bso-cent-xyz date

    to verify that no prompts occur between that session and a remote
    session for the *vtbuild-open* account on the *bso-cent-xyz* slave
    computer. This ensures that the jenkins account on the master will
    be able to run commands as the build account on the slave computer.

2.  in a terminal window for the *vtbuild-open* account on the
    *bso-cent-xyx* computer execute::

       vtbuild-open@bso-cent-xyz$ ssh vtbuild-open@git-open date
       vtbuild-open@bso-cent-xyz$ git clone ssh:://vtbuild-open@git-open/scm/vision/vtcmake.git

    and verify that no prompts occur. This ensures that the build
    account on the slave will be able to clone projects from git-open.

In the verification commands above, I use the notation of
``user@computer$`` to indicate the shell prompt *$* for the *user* on
the *computer*.


Configuring/Managing the Jenkins Master
=======================================

.. todo:: 

   add more details about configuring the jenkins master, slave, build
   jobs, test jobs, etc. 

JDK and Maven
-------------

As part of the system configuration, initialize the JDK and Maven
settings for the master.

Plugins
-------

Build Jobs
----------

Test Jobs
---------

Documentation Jobs
------------------

Slave Nodes
-----------

On the Master
~~~~~~~~~~~~~

Once you have a jenkins installed and running on a master, and a
second computer to which the jenkins account has ssh privileges, you
can then configure a jenkins node and jobs meant to run on that node. 

The slave itself
~~~~~~~~~~~~~~~~


Copy a Jenkins Master Configuration
===================================

There are at least two ways to copy a jenkins configuration from one
computer to another. 

1. You can manually copy the jobs (without the builds) and the plugins
   from the jenkins account and use them in a jenkins account on
   another computer. This requires you to know where files are.

2. You can use a thinBackup set. Do a "Backup Now" on the source
   computer and copy the timestamped subdirectory to the destination
   computer. You will need to edit the backed up file with the url
   location, so it uses the proper url for the jenkins installation on
   the destination computer. Point your browser to the jenkins server
   on the destination computer and go to the Manage Jenkins ->
   Plugins page and install the thinBackup plugin. Change the
   ThinBackup settings to point to the folder containing the backup
   set, and restore the configuration. (I successfully did this when
   copying a jenkins configuration from a vm on my laptop to a vm on
   the vtopen vcloud.)

Disable the Jenkins Service at Startup
======================================

.. code-block:: bash

   sudo sh -c "echo 'manual' > /etc/init/jenkins.override"
