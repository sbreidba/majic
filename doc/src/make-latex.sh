set -x
export src_dir=../..
export build_dir=../_build
sphinx-build -c . -d ${build_dir} -b latex ${src_dir} ${build_dir}/latex
pushd ${build_dir}/latex
make
cp clavin.pdf ../..
popd
