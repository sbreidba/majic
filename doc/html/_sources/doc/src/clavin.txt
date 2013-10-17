======
clavin
======

Lifecycle
=========

The correspondence between conventional cmake steps and maven steps
are:

#. **generate-sources**: is the cmake configure step; ``cmake
   -D... pathToSource``

#. **compile**: is the build step; ``make``

#. **test**: is the unit test step; ``make test``

#. **prepare-package**: is the generate documentation and install
   artifacts; ``make doc install``

#. **package**: is where the installed artifacts from the previous
   step are put into a tarball for maven to use in its *install* step;
   ``make tarball``, where the *tarball* target is a clavin
   replacement of the conventional make package cpack step, which we
   want to avoid right now because it is too hard to get the cmake
   package cpack step correct.

The complete list of maven lifecycle phases (obtained executing
:command:`mvn` with no arguments) are listed below. The highlighted
phases are for the corresponding cmake steps discussed above.

|    validate
|    initialize
| **generate-sources**
|    process-sources
|    generate-resources
|    process-resources
| **compile**
|    process-classes
|    generate-test-sources
|    process-test-sources
|    generate-test-resources
|    process-test-resources
|    test-compile
|    process-test-classes
| **test**
| **prepare-package**
| **package**
|    pre-integration-test
|    integration-test
|    post-integration-test
|    verify
|    install
|    deploy
|    pre-clean
|    clean
|    post-clean
|    pre-site
|    site
|    post-site
|    site-deploy


