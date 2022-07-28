#!/bin/sh

if [ "$#" -ne 1 ]; then
  echo "Provide the path to analyse"
  exit 1
fi

path=$1

script_dir=$(dirname $(realpath $0))

echo ""
echo "################################"
echo "###   Abstract syntax tree   ###"
echo "################################"
echo "path: $path"
$script_dir/phpjoern/php2ast $path
echo ""

echo ""
echo "##################################"
echo "###   Control Property Graph   ###"
echo "##################################"
$script_dir/joern/phpast2cpg nodes.csv rels.csv
echo ""
