# Business Central post-commit Git Hooks Integration

This is a sample project that shows how to setup Business Central to automatically push every content change to GitHub.

## New Integrations:
 - GitHub Enterprise
 - GitLab
 - GitLab Enterprise
 - Bitbucket API V2

## New Features
 - Configuration file template auto generation : Generating template config file ~/.gitremote
 - Configuraiton check: check for mandatory parameters in config file
 - Ignore pushing: ignore pushing projects with project name matching patterns defined in ignore parameter in config file.
 - Token authentication: add support to use token for both gitlab and github
 - Descriptive messages: add some output messages to instruct users, and provide info.
 - GitLab Groups: add support to create projects under specific GitLab groups and subgroups.
 - GitHub Organizations: add support to create repositories under specific GitHub Organizations. 
 - BitbucketTeams: add support to create repositories under specific BitBucket Teams.

## How to build

Clone this repository and build it locally, for this you need `Git`, `Maven` and `JDK 8`.

```shell
$ git clone https://github.com/porcelli/bc-git-integration-push.git
$ cd bc-git-integration-push
$ mvn clean install
$ mkdir -p $APP_SERVER_HOME/hooks/ && cp target/git-push-2.2-SNAPSHOT.jar $APP_SERVER_HOME/hooks/
```

## How to setup

The application uses a configuration file `.gitremote` that is located under the user home directory, if you don’t want to start from scratch, you can start running the jar file for the first time after the setup above or just as java -jar git-push-2.2-SNAPSHOT.jar, the application will generate a template configuration file in $HOME/.gitremote that you can follow and modify.

### Example “.gitremote”

```
#This is an auto generated template empty property file
provider=GIT_HUB
login=
password=
token=
remoteGitUrl=https://api.github.com/
useSSH=false
ignore=.*demo.*, test.*
githubOrg=OrgName
gitlabGroup=Group/subgroup
```
### Parameters:

 - **provider:** This is the flavor of the Git provider, currently only two values are accepted: GIT_HUB and GIT_LAB. Mandatory.
 - **login:** username. Mandatory.
 - **password:** plain text password. Not mandatory if token is provided.
 - **token:** this is the generated token to replace username/password unsecure connection, if this is not set you will get a warning that you are using an unsecured connection. Not mandatory if password is provided.
 - **remoteGitUrl:** This can be either a public provider URL or locally hosted enterprise for any provider. Mandatory.
 - **useSSH:** use the SSH protocol to push changes to the remote repository. Optional, default = false. Note: this config uses BC local ~/.ssh/ directory to obtain SSH config.
 - **ignore:** This is a comma separated regular expressions to ignore the project names that matches any of these expressions. Optional.
 - **githubOrg:** If GitHub is used as provider, it's possible to define the repository organization in this property. Optional.
 - **gitlabGroup:** If GitLab is used as provider, it's possible to define the group/subgroup in this property. Optional.
 - **gitlabGroup:** If BitBucket is used as provider, it's to define your BitBucket Team name in this property. Optional.

### Note:
 - GitLab only supports token authentication
 - Bitbucket only supports token authentication
 - Public GitHub url should be the api url ex: api.github.com and not www.github.com
 - Public Bitbucket url should be the api url ex: api.bitbucket.org and not www.bitbucket.com

## Enabling and Running

Create a post-commit hook template for Business Central and, finally, start the Business Central with the `org.uberfire.nio.git.hooks` properly set.

```shell
$ cd $APP_SERVER_HOME
$ echo "#\!/bin/bash\njava -jar $APP_SERVER_HOME/hooks/git-push-2.2-SNAPSHOT.jar" > hooks/post-commit
$ chmod 755 hooks/post-commit
$ ./bin/standalone.sh -c standalone-full.xml -Dorg.uberfire.nio.git.hooks=$APP_SERVER_HOME/hooks/
```

**Important note:** remember to replace `$APP_SERVER_HOME` by the real path of your application server home, in my case: `$HOME/jboss-eap-7.2/`. 

## Contributors

Spacial thank you for [AlyIbrahim](https://github.com/AlyIbrahim) for all valuable enhancements.

## License

This code is released under Apache 2 License.

Check [LICENSE](LICENSE-ASL-2.0.txt) file for more information.
