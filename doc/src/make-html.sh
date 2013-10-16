set -x
export src_dir=../..
export build_dir=../_build
sphinx-build -c . -d ${build_dir} -b html ${src_dir} ${build_dir}/html
