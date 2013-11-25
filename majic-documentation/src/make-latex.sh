set -x
export src_dir=../..
export bld_dir=_build
export builder=latex
sphinx-build -c ${src_dir} -d ${bld_dir} -b ${builder} ${src_dir} ${bld_dir}/${builder}
pushd ${bld_dir}/${builder}
make
popd
cp ${bld_dir}/${builder}/clavin.pdf site/sphinx
