package porcelli.me.git.integration.githook.push.command;

import java.nio.file.Path;

public class GetRepoName implements Command {

    public String execute(final Path currentPath) {
        return currentPath
                          .getName(currentPath.getNameCount() - 1)
                          .toString()
                          .replaceAll("\\.git", "");
    }
}
