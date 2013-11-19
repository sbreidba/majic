
// From simple untar testing
File simpleUntarFile = new File(basedir, "../out-of-source-test/untar-test/moduleDoc/module.txt");
assert(simpleUntarFile.isFile());

// From untar-dependencies
File untarDependenciesFile = new File(basedir, "../out-of-source-test/exports/cmake/FindAce.cmake");
assert(untarDependenciesFile.isFile());