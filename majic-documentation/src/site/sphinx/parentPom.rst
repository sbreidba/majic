====================
The Majic Parent POM
====================

The Majic pom file at :download:`majic-parent/pom.xml <../../../../majic-parent/pom.xml>` should be
used as the parent pom file for all pom files conforming to the Majic
conventions for building CMake-based C++ projects. You make this the
parent pom by using the following lines in your :file:`pom.xml`

Using the Majic Parent POM
==========================

.. literalinclude:: ../../../../majic-parent/pom.xml
   :language: xml
   :start-after: <!-- majic-documentation:gavc -->
   :end-before: <!-- majic-documentation:gavc -->
   :prepend: <parent>
   :append: </parent>

For multi-module builds, the above lines would be placed into the
aggregator pom and individual modules would make their parent (the aggregator) be their
primary parent.

Properties Defined
==================

A few properties are defined to make configuration of Majic projects simpler.
Some of these come from the parent pom, and some are injected when the majic-cmake
lifecycle is used.

.. todo::
   provide a link back to the plugin docs

.. literalinclude:: ../../../../majic-parent/pom.xml
   :language: xml
   :start-after: <!-- majic-documentation:properties -->
   :end-before: <!-- majic-documentation:properties -->

Profiles
========

Profiles are used in the Majic parent pom to detect the compilation environment
and generate appropriate artifacts. For example, under Windows, the specific variant of compiler to
used is detected by Majic and the "best available" compiler is selected:

.. literalinclude:: ../../../majic-parent/pom.xml
   :language: xml
   :start-after: <!-- majic-documentation:compiler-profile-example -->
   :end-before: <!-- majic-documentation:compiler-profile-example -->

Note that variables set in profiles are lower priority than those specified by the user. For example,
the following command-line would override the detection of the compiler (which is used during the CMake
configuration phase):

.. code-block::
    mvn install -Dcmake.compiler=vc2008

Build Element
-------------

The first thing done here is to define the maven build directory to
be the value of the property :index:`alt.build.directory`. The
remaining major sections are for build plugin management, and the
definition of the organization and description metadata values.

.. literalinclude:: ../../../majic-parent/pom.xml
   :language: xml
   :start-after: </profiles>






