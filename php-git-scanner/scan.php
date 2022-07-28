<?php

// This file is generated by Composer
require_once __DIR__ . '/vendor/autoload.php';
require_once 'helpers.php';
require_once 'workWithRepo.php';

include_once 'config.php';
if (!isset($CONFIG)) {
  error('Make sure to create a valid config.php file', 'Fatal');
}

/* --- Get input --- */
$optionValues = getopt(
  implode(array_keys($options), ':') . ':h',
  array_merge(array_map(function(array $item): string { return $item['name'] . ':'; }, $options), ['help']),
);

if (isset($optionValues['h']) || isset($optionValues['help'])) {
    die(printHelp($options) . "\n");
}

// Save options to variables
foreach ($optionValues as $key => $value) {
  $varname = strlen($key) == 1 ? $options[$key]['name'] : $key;
  ${$varname} = $value;
}

/* --- Validate input --- */
$count = (int)$count;
if ($count < 1 || $count > 100) {
  error('Number of repositories to fetch must be an integer between 1 and 100', 'Input');
}

if ($topic != '' && !preg_match('/^[a-z-0-9]+$/i', $topic)) {
  error('Topic must consist of alphanumeric characters or dashes', 'Input');
}

if ($stars != '' && !preg_match('/^[0-9]+$/i', $stars)) {
  error('Minimal star count must be a non-negative integer', 'Input');
}

if ($size != '' && !preg_match('/^(([<>]=?)?[0-9]+|[0-9]+\.\.[0-9]+)$/i', $size)) {
  error('Size must be a non-negative integer, a range (n..m) or a comparison (>n, <n, >=n, <=n)', 'Input');
}

$allowedOrderBys = $options['o']['values'];
if (!in_array($orderBy, $allowedOrderBys)) {
  error('Order by must be one of the following: ' . implode($allowedOrderBys, ', '), 'Input');
}

$allowedOrderDirections = $options['d']['values'];
if (!in_array($orderDirection, $allowedOrderDirections)) {
  error('Order direction must be one of the following: ' . implode($allowedOrderDirections, ', '), 'Input');
}

/* --- Build query --- */
$searchQuery = 'language:php';

if ($query != '')
  $searchQuery .= " $query in:name,description,readme";

if ($stars != '')
  $searchQuery .= " stars:>=$stars";

if ($topic != '')
  $searchQuery .= " topic:$topic";

if ($size != '')
  $searchQuery .= " size:$size";

$client = new \Github\Client();

if (isset($CONFIG['githubToken']) && $CONFIG['githubToken'] != '') {
  $client->authenticate($CONFIG['githubToken'], null, Github\AuthMethod::ACCESS_TOKEN);
}

$api = $client->api('search');

// Fetch this many at once, others on demand
$perPage = min($count, 5);

$paginator = new Github\ResultPager($client, $perPage);
$parameters = [$searchQuery, $orderBy, $orderDirection];
$repositories = $paginator->fetchAllLazy($api, 'repositories', $parameters);

$i = 0;

/* --- Work with repositories --- */
foreach ($repositories as $key => $repo) {
  if ($i++ >= $count) {
    break;
  }

  useRepo($repo);
}

if ($i == 0) {
  error('No matching repositories found', 'Search');
}
