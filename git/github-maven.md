# Publishing Java packages with Maven

Create a Reusable Spring Boot Library (Custom Dependency) and host it in a free remote Maven repositories such as GitHub Packages

## Publish to GitHub Packages

- Create a repository

- Create a Personal Access Token (PAT) with write:packages and read:packages scopes

- Modify pom.xml to include GitHub Packages (value of id must be github)
  ```
  <distributionManagement>
    <repository>
      <id>github</id>
      <url>https://maven.pkg.github.com/kelvn-dev/meikocn-common</url>
    </repository>
  </distributionManagement>
  ```

- Config settings.xml and publish by running command locally (Not recommended)
  ```
  mvn clean deploy
  ```
  ```
  <settings>
    <servers>
      <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_PAT</password>
      </server>
    </servers>
  </settings>
  ```

- Config a ci/cd pipeline to publish on release by creating GitHub Secrets and Workflow File (recommended)

- Bonus: create and publish a GitHub release entirely from the CLI
  ```
  git tag v1.0.0
  git push origin v1.0.0
  gh release create v1.0.0  # first logged in with gh auth login
  ```
  
## Use the Dependency in Another Project

- Add GitHub Packages as a repository in pom.xml
  ```
  <repositories>
    <repository>
      <id>meikocn-common</id>
      <url>https://maven.pkg.github.com/kelvn-dev/meikocn-common</url>
    </repository>
  </repositories>
  ```

- Add the dependency in pom.xml
  ```
  <dependency>
    <groupId>com.meikocn</groupId>
    <artifactId>meikocn-common</artifactId>
    <version>1.0.9</version>
  </dependency>
  ```

- Config GitHub credentials in settings.xml (located at .m2)
  ```
  <settings>
    <servers>
      <server>
        <id>meikocn-common</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_PAT</password>
      </server>
    </servers>
  </settings>
  ```