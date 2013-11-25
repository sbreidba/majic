set -x
export src_dir=../..
export bld_dir=_build
export builder=html
sphinx-build -c ${src_dir} -d ${bld_dir} -b ${builder} ${src_dir} ${bld_dir}/${builder}
