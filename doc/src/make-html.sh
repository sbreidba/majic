set -x
export src_dir=site/sphinx
export build_dir=_build
sphinx-build -c ${src_dir} -d ${build_dir} -b html ${src_dir} ${build_dir}/html
