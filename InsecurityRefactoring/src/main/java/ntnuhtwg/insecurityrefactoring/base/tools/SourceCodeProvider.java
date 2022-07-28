package ntnuhtwg.insecurityrefactoring.base.tools;

import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring.PIPRenderer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SourceCodeProvider {

    private final String inputPath;

    private String filesystemPath;

    private final String reposSubfolder = "git";

    private PIPRenderer pipRenderer;


    public SourceCodeProvider(String inputPath, PIPRenderer pipRenderer) {
        this.inputPath = inputPath;
        this.pipRenderer = pipRenderer;
        this.parseInputPath();
    }

    private void parseInputPath() {
        Pattern gitRepoPattern = Pattern.compile("((git|ssh|http(s)?)|(git@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(\\.git)?(/)?");
        boolean isGitRepo = gitRepoPattern.matcher(this.inputPath).find();

        if (!isGitRepo)
            this.filesystemPath = this.inputPath;
        else
            this.cloneGitRepo();
    }

    private void cloneGitRepo() {
        pipRenderer.setClonningRepoText();
        File folder = new File(GlobalSettings.dataFolder + "/" + this.reposSubfolder);
        if (!folder.exists())
            folder.mkdir();

        String repoFolderName = this.inputPath.replaceAll("/$", "");    // trim last slash
        String[] split = repoFolderName.split("/");
        repoFolderName = split[split.length - 1];

        if (repoFolderName.endsWith(".git"))
            repoFolderName = repoFolderName.substring(0, repoFolderName.length() - 4);

        File repoFolder = new File(GlobalSettings.dataFolder + "/" + this.reposSubfolder + "/" + repoFolderName);

        System.out.println(repoFolder.getAbsolutePath());

        try {
            if (repoFolder.exists())
                FileUtils.deleteDirectory(repoFolder);

            // Clone repo
            String clone = "git clone " + this.inputPath;
            Util.runCommand(clone, folder);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(GitUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.filesystemPath = repoFolder.getAbsolutePath();
    }

    public String getFilesystemPath() {
        return this.filesystemPath;
    }

}
