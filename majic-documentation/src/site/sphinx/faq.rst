.. _faq:

FAQ
========

Maven Questions
---------------

In the pom file, what is the difference between <dependencies> and <dependencyManagement>?
   <dependencies> tells Maven: "I am dependent on these thing".

   <dependencyManagement> tells Maven: "If my pom file, or another child pom file within my project uses a dependency
   that's listed here, I would like you to use the specified version and scope".

   Projects should specify version numbers and scope in either <dependencyManagement> or <dependencies>, but not both.
   Using <dependencyManagement> is a best practice when there's more than one pom file in a specific project,
   as it consolidates the version numbers into one place, ensuring consistency.
   If there's only one pom file, that's not very interesting.

What happens if there are dependency conflicts? Meaning, I depend on a:2.0, but a transitive dependency depends on a:1.0?
   By default, Maven will select the later dependency (in this case a:2.0) and use it. This might not work, of course,
   if things that need version 1.0 of a really need it (and cannot work with version 2.0)!

How do I detect dependency conflicts?
   Use the Maven enforcer plugin, and enable the dependencyConvergence rule. Note that now you might get
   spurious errors - in the above example, let's say that something that builds with a:1.0 works quite well with a:2.0.
   In this situation, you can exclude a:1.0 from our dependency. Take a look at the Maven documentation on
   `Dependency Exclusions <https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html>`_.

Wait, you just said that <dependencyManagement> solves this problem! How does it differ from the enforcer's rule?
   <dependencyManagement> affects the project that I'm building right now. It can't alter things that I depend on.
   The dependencyConvergence enforcer rule can detect cases where *already built* projects have conflicting
   dependencies.

What are some useful Maven commands to run?
   Take a look at :ref:`helpful-maven-commands`.
