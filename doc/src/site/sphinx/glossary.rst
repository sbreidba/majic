========
Glossary
========

.. glossary::
   :sorted:
 
   CMAKE_INSTALL_PREFIX
      The prefix directory to which cmake will install a project.

   external package
      A packages that is *external* to a project under consideration. The
      notion of *external* is relative to the context in which it is
      being used. Generally, external packages change infrequently,
      details of its implementation are not relevant or of interest, may
      be large in size, etc. The term *external* is used in contrast to
      :term:`internal package`. An external package will be treated
      differently from an internal package when it comes to its installed
      location and the rules or goals used when cleaning. External
      packages will be installed to a common location and will be cleaned
      explicitly; not as part of the conventional clean goal, which is
      considered a *lightweight* clean step.

   internal package
      A package that is *internal* to a project under consideration. The
      notion of *internal* is used to distinguish with 
      :term:`external package`. Internal packages are installed in a
      in the same location as project artifacts and are cleaned by the
      clean goal.
  
