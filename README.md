# Process Automation Manager post-commit Git Hooks Integration

This project is forked from https://github.com/porcelli/bc-git-integration-push which is a sample project that shows how to setup Business Central to automatically push every content change to GitHub.

## New Integrations:
 - GitHub Enterprise
 - GitLab
 - GitLab Enterprise
 - Bitbucket (Coming soon)

## New Features
 - Configuration file template auto generation : Generating template config file ~/.gitremote
 - Configuraiton check: check for mandatory parameters in config file
 - Ignore pushing: ignore pushing projects with project name matching patterns defined in ignore parameter in config file.
 - Token authentication: add support to use token for both gitlab and github
 - Descriptive messages: add some output messages to instruct users, and provide info.

## How to build

Clone this repository and build it locally, for this you need `Git`, `Maven` and `JDK 8`.

```shell
$ git clone https://github.com/AlyIbrahim/pam-githook-integration.git
$ cd pam-githook-integration
$ mvn clean install
$ mkdir -p $APP_SERVER_HOME/hooks/ && cp target/git-push-2.0-SNAPSHOT.jar $APP_SERVER_HOME/hooks/
```

## How to setup

Create the post-commit hook template for Business Central and, finally, start the Business Central with the `org.uberfire.nio.git.hooks` properly set.

```shell
$ cd $APP_SERVER_HOME
$ echo "#\!/bin/bash\njava -jar $APP_SERVER_HOME/hooks/git-push-1.0-SNAPSHOT.jar" > hooks/post-commit
$ chmod 755 hooks/post-commit
$ ./bin/standalone.sh -c standalone-full.xml -Dorg.uberfire.nio.git.hooks=$APP_SERVER_HOME/hooks/
```

**Important note:** remember to replace `$APP_SERVER_HOME` by the real path of your application server home, in my case: `$HOME/jboss-eap-7.2/`. 

## How to configure

The application uses a configuration file `.gitremote` that is located under the user home directory, if you don’t want to start from scratch, you can start running the jar file for the first time after the setup above or just as java -jar git-push-1.0-SNAPSHOT.jar, the application will generate a template configuration file in $HOME/.gitremote that you can follow and modify.

### Example “.gitremote”

```
#This is an auto generated template empty property file
#Tue Apr 23 18:33:13 CDT 2019
login=
password=
token=hdy2Fhd63Gi27h3IJqw
remoteGitUrl=https://api.github.com
provider=GitHub
ignore=.*demo.*, test.*
```
### Parameters:

 - **login:** username.
 - **password:** plain text password .
 - **token:** this is the generated token to replace username/password unsecure connection, if this is not set you will get a warning that you are using an unsecured connection.
 - **remoteGitUrl:** This can be either a public provider URL or locally hosted enterprise for any provider.
 - **provider:** This is the flavor of the Git provider, only 3 values are accepted here: GitHub, GitLab and BitBucket when it is supported.
 - **ignore:** This is a comma separated regular expressions to ignore the project names that matches any of these expressions

### Note: 
 - GitLab only supports token authentication
 - Github url should be the api url ex: api.github.com and not www.github.com


## License

This code is released under Apache 2 License.

Check [LICENSE](LICENSE-ASL-2.0.txt) file for more information.
