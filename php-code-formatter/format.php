<?php

require __DIR__ . '/vendor/autoload.php';

use gossi\formatter\Formatter;

if (count($argv) != 2) {
    die('Please specify a path to file for reformatting (e.g. `php format.php /home/data/file.php`)\n');
}

// Get number of file lines from output of 'wc' command
function getLineCountFromWc(string $wc): int {
  return explode(' ', trim($wc))[0];
}

$file = $argv[1];

$fileDir = substr($file, 0, -strlen(basename($file)));

$originalContent = file_get_contents($file);
$fileSize = strlen($originalContent);
$fileIsBig = $fileSize > 10000;
$fileIsHuge = $fileSize > 50000;
$originalFileLines = getLineCountFromWc(exec('cat ' . $file . ' | wc'));

$reformattedFiles = [];

// Try all reformatting profiles
foreach (glob(__DIR__ . '/profiles/*.yml') as $index => $profile) {
  // Use just 4 configurations for files over 10kb and 2 over 50kb
  if (($index > 3 && $fileIsBig) || ($index > 1 && $fileIsHuge)) {
    break;
  }

  $formatter = new Formatter($profile);
  $reformatted = $formatter->format($originalContent);

  $newFile = $fileDir . $index . '___reformatted___' . basename($file);

  file_put_contents($newFile, $reformatted);

  $linesChanged = $fileIsBig || $fileIsHuge ? 0 : getLineCountFromWc(exec('sdiff -B -b -s ' . $file . ' ' . $newFile . ' | wc'));
  $reformattedFiles[$index] = [
    'path' => $newFile,
    'diff' => ($linesChanged / $originalFileLines) * 100,
  ];
}

if ($fileIsBig || $fileIsHuge) {
  $suitableReformats = $reformattedFiles;
} else {
  // Keep only those reformats with >= 60% diff (if it's < 3 reformats, keep 50% diff, or 40% etc.)
  $minimalDiff = 60;
  do {
    $suitableReformats = array_filter($reformattedFiles, function($test) {
      global $minimalDiff;
      return $test['diff'] >= $minimalDiff;
    });
    $minimalDiff -= 10;
  } while(count($suitableReformats) < 3);
}

$randomReformat = $suitableReformats[array_rand($suitableReformats)]['path'];

rename($randomReformat, $file);

// Delete all reformats
foreach ($reformattedFiles as $file) {
  if (file_exists($file['path'])) {
    unlink($file['path']);
  }
}
