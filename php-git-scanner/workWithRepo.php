<?php

require_once 'config.php';
require_once 'helpers.php';

function useRepo(array $repo): void {
  global $CONFIG;

  $repoPath = $CONFIG['baseDir'] . '/' . $repo['name'];

  deleteNonEmptyDir($repoPath);

  echo '--- ' . $repo['full_name'] . ': ' . $repoPath . " ---\n";

  // Clone the repo
  echo "   Cloning... ";
  $output = explode("\n", trim(shell_exec('cd ' . $CONFIG['baseDir'] . ' && git clone ' . $repo['clone_url'] . ' ' . $repoPath . ' 2>&1; echo $?')));
  $exitCode = end($output);

  if ($exitCode != '0') {
    array_pop($output);
    error(implode("\n", $output), 'Clone');
  }

  echo "cloned.\n";

  // Scan for PIPs
  echo "   Scanning for PIPs... ";
  $output = explode("\n", trim(shell_exec('cd ../InsecurityRefactoring && ./run_insec.sh -p ' . $repoPath . ' 2>&1; echo $?')));
  $foundPipsString = 'FOUND PIPs: ';

  // Search output from the end until `$foundPipsString` is found
  for (end($output); key($output)!==null; prev($output)){
    $currentElement = current($output);

    if (!str_starts_with($currentElement, $foundPipsString))  {
      continue;
    }

    $foundPips = (int)substr($currentElement, strlen($foundPipsString));

    // No PIPs found -> delete the cloned repo
    if ($foundPips == 0) {
      echo "no PIPs found, deleting.\n";
      deleteNonEmptyDir($repoPath);
    } else {
      if ($foundPips == 1) {
          echo $foundPips . " PIP found! Rescan with GUI to exploit it.\n";
      } else {
          echo $foundPips . " PIPs found! Rescan with GUI to exploit them.\n";
      }
    }

    echo "\n";
    return;
  }

  // `$foundPipsString` not found
  echo "invalid scanner output.\n\n";
}
