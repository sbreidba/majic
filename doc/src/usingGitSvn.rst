======================
git clone of boost-svn
======================

.. _boost-svn on github:

Clone of boost-svn on github
============================

(The content in this subsection came from `here
<http://boost.2283326.n4.nabble.com/Live-read-only-GIT-mirrors-of-Boost-trunk-SVN-td4646063.html>`_.)

So I decided to try out a new Subversion <=> Git mirroring tool,
SubGit v2.0 EAP (http://subgit.com/eap/) whose author very kindly gave me a
free open source licence and the result is at:

https://github.com/ned14/boost-trunk

All branches and tags, as far as I can tell, are mirrored correctly.

Clone of github boost on git-open
=================================

Url: ``git-open/scm/3rdparty/boost-trunk`` (this has also been
softlinked to :file:`boost.git`).

This repo was made by cloning the github ned14/boost-trunk repo with
the additional option of ``--bare``.

============================================
Selecting branches when cloning with git-svn
============================================

.. sidebar:: git-svn is Fragile
   
   My very limited experience agrees with the comments in the links
   reference :ref:`above <boost-svn on github>` that mention
   :command:`git-svn` usage is fragile and error prone. I spent over 8
   hours trying to ``git svn clone`` the boost subversion
   repository. Sometime between 8 and 14 hours (I have to sleep
   sometimes!), the clone failed and had to be removed to restart. So
   I gave up git cloning the boost svn repo after I found the
   ned14/boost-trunk repo on git hub.

   While :command:`git svn` did not work for me with a huge svn
   codebase like boost, it did work when I cloned just the 1.54
   release branch of boost. So :command:`git svn` can and does
   work. So this section may still be helpful if you use a 3rd party
   project that uses subversion.

(The content in this subsection came from `this gist
<https://gist.github.com/trodrigues/1023167>`_ on github.)

If you want to clone an svn repository with git-svn but don't want it
to push all the existing branches, here's what you should do.

* Clone with git-svn using the -T parameter to define your trunk path
  inside the svnrepo, at the same time instructing it to clone only
  the trunk::
  
     git svn clone -T trunk http://example.com/PROJECT

* If instead of cloning trunk you just want to clone a certain branch,
  do the same thing but change the path given to -T::

     git svn clone -T branches/somefeature http://example.com/PROJECT

This way, git svn will think that branch is the trunk and generate the
following config on your .git/config file::

    [svn-remote "svn"]
	    url = https://example.com/
	    fetch = PROJECT/branches/somefeature:refs/remotes/trunk

* If at any point after this you want to checkout additional branches,
  you first need to add it on your configuration file::

    [svn-remote "svn"]
	    url = https://example.com/
	    fetch = PROJECT/branches/somefeature:refs/remotes/trunk
	    branches = PROJECT/branches/{anotherfeature}:refs/remotes/*

The branches config always needs a glob. In this case, we're just
specifying just one branch but we could specify more, comma separating
them, or all with a \*.

* After this, issue the following command::

   git svn fetch

Sit back. It's gonna take a while, and on large repos it might even
fail. Sometimes just hitting CTRL+C and starting over solves it. Some
dark magic here.

* After this, if you issue a git branch -r you can see your remote
  branch definitions::

     git branch -r
  
     anotherfeature

* Now you can add a local branch which tracks the remote svn branch::

     git branch --track myanotherfeature remotes/anotherfeature

Try not to use the same branch name for the local one if you don't
wanna mess it up easily.
