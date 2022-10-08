## Open issues

If you want to suggest a new feature or issue a bug, you will have to create an issue. **Before creating an issue, please make sure there is no related issue yet.** If there is already a related issue, please comment in it describing your point of view or how you reproduced the bug to help the contributors understand your needs. 

If there is no related issue, feel free to create an issue with the label 'to be qualified'. It will be qualified by one of the maintainers.

## Contribute code

### Your first contribution

You are new to the project and would love to help develop Datamaintain? First of all, welcome! Now, you are looking for some issues to tackle. You may find some issues [labelled with good first issue](https://github.com/4sh/datamaintain/contribute) that are adapted to newcomers. If there is no good first issues left, feel free to search for issues you may be comfortable with and that are not already assigned to a maintainer. You may find them [here](https://github.com/4sh/datamaintain/issues?q=is%3Aopen+no%3Aassignee). If an issue seems unclear, feel free to ask precisions in that issue.

### Style guides

#### Commit messages

Commits should be formed like this:
```
<type>: <issue linked to your commit> <commit message>
```

* type: feat, fix, chore, docs, refactor or test, depending on the nature of your commit 
* issue linked to your commit: reference to the issue you are solving
* commit message: a description of what your commit adds to Datamaintain. Your commit message should help others understand exactly what you did just reading it.

Example:

```feat: #80 Introduce notion of whitelisted tags ```

This commit adds the whitelisted tags feature, which is described by the issue #80.

#### Coding conventions

* use 4 spaces to indent
* always add brackets when you write an ``if`` or a ```for```
### Unit tests

Before committing, please check the tests are still passing. To maintain the library quality, you will have to write unit tests for every enhancement and every bug you may be correcting and ensure that your code does not break the tests that were already there.

The tests libraries used in Datamaintain are the following:
* [JUnit 5](https://junit.org/junit5/) to implement the tests
* [strikt](https://strikt.io) for more assertions
* [mockk](https://mockk.io/) to mock your classes

To write your test, you will have to follow some conventions:
* The test class name has to be the main class followed by ```Test```. For example,the test class of the class ```MyClass``` would be named ```MyClassTest```.
* Your test class has to have the same package as the class you are testing and be in the ```test``` directory of your module.
* Test functions concerning the same functionality should be in an inner class with the ```@Nested``` annotation, inside your test class. 
* The name of a test function begins with 'should' and describes what behaviour is expected of the tested class in that test.
* A test is divided in three parts: given, when and then. You may indicate them using comments.
    * _given_: declaration of the different arguments given to the method tested
    * _when_: action performed in the test, usually calling a method from the class
    * _then_: assertion about what is expected. **One test function corresponds to one assertion.**

Here is an example of a test function.

```
@Test
fun `should do what I want`() {
    // Given
    val initialValue = "myInitialValue"
   
    // When
    val result = myClass.doSomething(initialValue)
   
    // Then
    expectThat(result).isEqualTo("expectedResult")
}
```

### Branches & pull requests

When you create a branch to solve an issue, the branch must be created from ```dev``` and be named like that:

```<issue>-issue-description```

An example:

```#71-relative-path-matchers```

This branch solves the issue #71 and concerns relative path matchers.

Once you are done solving the issue you were working on, please submit a pull request with ```dev``` as the base. Please make sure to respect the [pull request template](https://github.com/4sh/datamaintain/blob/dev/.github/pull_request_template.md) when opening a pull request.

### Requirements to run the tests

The [mongo integration tests](./modules/test/src/test/kotlin/datamaintain/test/MongoIT.kt) use both the [mongo cli](https://www.mongodb.com/docs/mongocli/stable/) and the [mongodb shell](https://www.mongodb.com/docs/mongodb-shell/) so you will need them to run the integration tests. Since the [CI](https://github.com/4sh/datamaintain/actions/workflows/CI.yaml) does run the integration tests on every push, you shouldn't worry too much about it, except if the build failed on the CI
