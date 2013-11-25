==================
The Bob Parent POM
==================

The Bob pom file at :download:`/cmake-maven-parent/pom.xml` should be
used as the parent pom file for all pom files conforming to the Bob
conventions for building cmake based C++ projects. You make this the
parent pom by using the following lines in your :file:`pom.xml`

Using the Bob Parent POM
========================

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:gavc -->
   :end-before: <packaging>
   :prepend: <parent>
   :append: </parent>

For multi-module builds, the above lines would be placed into the
aggregator pom and individual modules would make their parent be their
primary parent.

Properties Defined in the Bob Parent Pom
========================================

All of the properties in the parent pom are shown in the snippet
below. Important properties include:

package.prefix.directory 
   the directory to which :term:`external package`'s will be
   installed. In cmake terms, this is the :term:`CMAKE_INSTALL_PREFIX`.

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:properties -->
   :end-before: <!-- bob:properties -->

Profiles
========

Profiles are used in the Bob parent pom to provide the custom build
logic unique to cmake builds of C++ projects. In the future many/most
of these profiles should be replaced with a more maven conventional
custom plugin. 

OS-Unix and OS-Windows
----------------------

The profile used on Unix/Linux.

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile unix -->
   :end-before: <!-- bob:profile unix -->


The profile used on Windows

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile win -->
   :end-before: <!-- bob:profile win -->

Aggregator Present
------------------

The profile activated when a pom file is in the presence of an
*aggregator* pom. This is simply a recognition that the parent folder
of the pom file also has a pom file.

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile aggregator -->
   :end-before: <!-- bob:profile aggregator -->

cmake
-----

This profile defines the default arguments used in each of the cmake
configure, build, and install steps. This profile hooks cmake
invocations into the :index:`process-sources`, :index:`compile`, and
:index:`prepare-package` phases of the lifecycle.

.. todo:: 

   Skip, why is the cmake test step not also in this profile?

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile cmake -->
   :end-before: <!-- bob:profile cmake -->

Unpack Packages
---------------

The profile that defines how to unpack package tarballs. This profile
hooks into the :index:`validate` phase of the lifecycle. 

.. todo::

   Skip, shouldn't this hook into the *initialize* phase that comes
   between validate and initialize?

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile unpack -->
   :end-before: <!-- bob:profile unpack -->

Create Package Tarballs
-----------------------

The profile that defines how to make package tarballs. This profile
hooks into the :term:`package` phase of the lifecycle.

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile package -->
   :end-before: <!-- bob:profile package -->

Attach Package
--------------

.. todo::
   
   What does this profile do?

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: <!-- bob:profile attach-package -->
   :end-before: <!-- bob:profile attach-package -->

Build Element
-------------

The first thing done here is to define the maven build directory to
be the value of the property :index:`alt.build.directory`. The
remaining major sections are for build plugin management, and the
definition of the organization and description metadata values.

.. literalinclude:: /cmake-maven-parent/pom.xml
   :language: xml
   :linenos:
   :start-after: </profiles>






