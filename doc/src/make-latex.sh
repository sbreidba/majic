set -x
export src_dir=site/sphinx
export build_dir=_build
sphinx-build -c ${src_dir} -d ${build_dir} -b latex ${src_dir} ${build_dir}/latex
pushd ${build_dir}/latex
make
popd
cp ${build_dir}/latex/clavin.pdf ${src_dir}
