/*
TODO! need to figure out how to get the build root here so that we can test with and without os subdirs
// From simple untar testing
File simpleUntarFile = new File(basedir, "../out-of-source-test/untar-test/moduleDoc/module.txt");
assert(simpleUntarFile.isFile());

// recursive search, since the OS directory may be appended to the build dir
def foundDependency = false;
def search;
findTxtFileClos = {
        it.eachDir(findTxtFileClos);
        it.eachFileMatch(search) {
            foundDependency = true;
        }
    }

search = "~/FindAce\.cmake/";
foundDependency = false;
findTxtFileClos(new File(basedir, "../out-of-source-test/exports"))
assert(foundDependency);
*/