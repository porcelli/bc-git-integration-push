# Post-commit hook GitHub Push Integration

This is a sample project that shows how to setup Business Central to automatically push every content change to GitHub.

## How to build

Clone this repository and build it locally, for this you need `Git`, `Maven` and `JDK 8`.

```shell
$ git clone https://github.com/porcelli/bc-git-integration-push.git
$ cd bc-git-integration-push
$ mvn clean install
$ mkdir -p $APP_SERVER_HOME/hooks/ && cp target/git-push-1.0-SNAPSHOT.jar $APP_SERVER_HOME/hooks/
```

## How to setup

Create a `.github` file in your home directory, with the following content:

```properties
login=<your-username>
password=<your-password-here>
```

Then create the post-commit hook template for Business Central and, finally, start the Business Central with the `org.uberfire.nio.git.hooks` properly set.

```shell
$ cd $APP_SERVER_HOME
$ echo "#\!/bin/bash\njava -jar $APP_SERVER_HOME/hooks/git-push-1.0-SNAPSHOT.jar" > hooks/post-commit
$ chmod 755 hooks/post-commit
$ ./bin/standalone.sh -c standalone-full.xml -Dorg.uberfire.nio.git.hooks=$APP_SERVER_HOME/hooks/
```

**Important note:** remember to replace `$APP_SERVER_HOME` by the real path of your application server home, in my case: `/Users/porcelli/jboss-eap-7.2/`. 

## License

This code is released under Apache 2 License.

Check [LICENSE](LICENSE-ASL-2.0.txt) file for more information.
