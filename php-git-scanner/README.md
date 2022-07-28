# About
This script searches for public GitHub PHP repositories based on given criteria, clones them and identifies if they contain any PIPs. If not, the cloned repository is deleted. If yes, it can be used further to insert vulnerabilities using the GUI.

# How to use
Make sure that the Insecurity Refactoring tool is built and can be run.

Create `config.php` file based on `config.example.php`.

Run script `php scan.php` to perform a search with default options.

Run `php scan.php -h` or `php scan.php --help` for more details on configuration.
