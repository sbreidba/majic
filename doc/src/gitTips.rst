========
git Tips
========

.. index:: git; graph

To see a git revision graph in text use::

   git log --oneline --decorate --graph

which has sample output::

   git log --oneline --decorate --graph
   * 283f667 (HEAD, master) prepare for merge to aspn-super by moving everything to aspn-vfc subdirectory
   * f212a14 make destructor signature nothrow exception specification match base class (for centos6 build)
   * 76d3d86 corect place to count the number of samples in AirDataTrainer.
   * f38601d make the HmfTsmController period_ parameter configurable. the default to 1.0 seconds.
   *   5de731e Merge branch 'master' of ssh://git-open.sarnoff.com/scm/vision/aspn-vfc into debug/compass-trainer
   |\  
   | *   8672f09 (tag: v2.1.6) Merge branch 'origin/feature/MultipleImu-Te2'
   | |\  
   | | *   83c00a4 Merge branch 'origin/master'
   | | |\  
   | | |/  
   | |/|   
   | | *   1b00a25 Merge branch 'origin/feature/MultipleImu-Te2'


.. index:: git; show tag

To get information about a git tag use::

   git show v2.1.6

which produces output::

   tag v2.1.6
   Tagger: Philip Miller <philip.miller@sri.com>
   Date:   Wed Oct 9 12:38:41 2013 -0400

   infrastructure is in place to support multiple IMUs

   commit 8672f097ab911a354d27269ef89f2f097e1ad51b
   Merge: 3708494 83c00a4
   Author: Philip Miller <philip.miller@sri.com>
   Date:   Wed Oct 9 12:26:32 2013 -0400

       Merge branch 'origin/feature/MultipleImu-Te2'
