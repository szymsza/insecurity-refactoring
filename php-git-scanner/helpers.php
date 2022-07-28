<?php

function error(string $message, string $type): void {
  die("$type error: $message\n");
}

function printHelp(array $options): string {
  $result = "\nThis script searches for public GitHub repositories based on given criteria, clones them and identifies if they contain any PIPs.\n\n";

  $result .= "Options:\n";

  foreach ($options as $short => $option) {
    $optionFlags = " -$short --" . $option['name'];
    $result .= $optionFlags;
    $result .= str_repeat(' ', 22 - strlen($optionFlags));
    $result .= $option['description'];

    if (isset($option['values'])) {
      $result .= '; one of ' . implode($option['values'], ', ');
    }

    $result .= "\n";
  }

  return $result;
}

function deleteNonEmptyDir(string $dir): void {
  system('rm -rf -- ' . escapeshellarg($dir));
}

// Default option values
$count = '5';
$size = '';
$topic = '';
$query = '';
$stars = '';
$orderBy = 'stars';
$orderDirection = 'desc';

$options = [
  'c' => [
    'name' => 'count',
    'description' => 'Number of repositories to fetch, default ' . $count,
  ],
  'o' => [
    'name' => 'orderBy',
    'description' => 'Property to order by, default ' . $orderBy,
    'values' => ['stars', 'forks', 'help-wanted-issues', 'updated'],
  ],
  'd' => [
    'name' => 'orderDirection',
    'description' => 'Order direction, default ' . $orderDirection,
    'values' => ['desc', 'asc'],
  ],
  't' => [
    'name' => 'topic',
    'description' => 'Use repositories with this topic; alphanumeric characters or dashes',
  ],
  's' => [
    'name' => 'stars',
    'description' => 'Use repositories with this amount of stars or more; positive integer',
  ],
  'z' => [
    'name' => 'size',
    'description' => 'Use repositories with this size in kBs; an integer (exact match), a range (n..m) or a comparison (>n, <n, >=n, <=n)',
  ],
  'q' => [
    'name' => 'query',
    'description' => 'Use repositories whose name, description or readme contains given query; any string',
  ],
];
