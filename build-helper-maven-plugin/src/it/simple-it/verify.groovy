
// From simple untar testing
File simpleUntarFile = new File(basedir, "../simple-it-build/untar-test/moduleDoc/module.txt");
assert(simpleUntarFile.isFile());

// From untar-dependencies
File untarDependenciesFile = new File(basedir, "../simple-it-build/exports/cmake/FindAce.cmake");
assert(untarDependenciesFile.isFile());