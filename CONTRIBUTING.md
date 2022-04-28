# Contributing Guidelines

This document contains information and guidelines about contributing to this project.
Please read it before you start participating.

**Topics**

* [Asking Questions](#asking-questions)
* [Reporting Security Issues](#reporting-security-issues)
* [Reporting Non Security Issues](#reporting-other-issues)
* [Commit Messages](#commit-messages)
* [Developers Certificate of Origin](#developers-certificate-of-origin)

## Asking Questions

Questions are welcome! We encourage you to ask questions through GitHub issues.
Before doing so, please check that the project issues database doesn't already
include an answer to your question. Then open a new Issue and use the "Question"
label.

## Reporting Security Issues

If you have discovered an issue with this code that could present a security hazard or wish to discuss a sensitive issue with our security team, please contact security@z.cash [security.asc](https://z.cash/gpg-pubkeys/security.asc). Key fingerprint = AF85 0445 546C 18B7 86F9 2C62 88FB 8B86 D8B5 A68C

## Reporting Non Security Issues

A great way to contribute to the project
is to send a detailed issue when you encounter a problem.
We always appreciate a well-written, thorough bug report.

Check that the project issues database
doesn't already include that problem or suggestion before submitting an issue.
If you find a match, add a quick "+1" or "I have this problem too."
Doing this helps prioritize the most common problems and requests.

When reporting issues, please include the following:

* The version of IDE you're using
* The version of Operating System you're targeting
* The full output of any stack trace or compiler error
* A code snippet that reproduces the described behavior, if applicable
* Any other details that would be useful in understanding the problem

This information will help us review and fix your issue faster.

## Pull Requests

We **love** pull requests! 

All contributions _will_ be licensed under the MIT license.

Code/comments should adhere to the following rules:

* Every Pull request must have an Issue associated to it. PRs with not 
associated with an Issue will be closed
* Code build and Code Lint must pass.
* Names should be descriptive and concise.
* Although they are not mandatory, PRs that include significant testing will be
prioritized.
* All enhancements and bug fixes need to be documented in the CHANGELOG.
* When writing comments, use properly constructed sentences, including
  punctuation.
* When documenting APIs and/or source code, don't make assumptions or make
  implications about race, gender, religion, political orientation or anything
  else that isn't relevant to the project.
* Remember that source code usually gets written once and read often: ensure
  the reader doesn't have to make guesses. Make sure that the purpose and inner
  logic are either obvious to a reasonably skilled professional, or add a
  comment that explains it.

## Commit Messages

Commit history is an important part of the project's documentation.
Besides its obvious testimonial value, commits represent a point in time
in the project's lifetime in a given context. A good record of the changes that
occurred during the project's life helps to guarantee that it can outlive its
stakeholders no matter how foundational or crucial these individuals (or
groups) were. As any reading material, it is best appreciated and comprehended
when there's a visible structure that readers can follow and reason about. 

For that we've defined a structure for commit messages that all contributors must
follow to maintain coherence on the project's commit log. The proposed format
has been inspired by [this great article](https://cbea.ms/git-commit/)


### Preparing to contribute to the project
The first thing you should look for is an existing issue. It is possible
that the contribution you are planning to work on was already discussed
by other users and/or contributors in the past. If not present, file an 
issue following the criteria described in the preceeding sections.

Every contribution must reference an existing Issue. This issue is important
since it will be directly referenced in the title of your commit. 

Although we prefer small PR's. We encourage our contributors to use Squash
commits extensively. Maintainers prefer avoiding _merge commits_ when possible. 
It is very much likely that _if accepted_, your contribution will be _squash merged_.

When squashing commits, use your best judgement. In some situations, a refactoring may
be done before actual behavior changes are implemented. It is reasonable to keep such
a refactoring as a separate commit as it both makes review easier and allows for 
these refactoring commit SHAs to be added to `.git-blame-ignore-revs`.

### Structuring a PR Commit

#### Commit Title
The first line of your commit message constitutes its _title_. Maintainers will 
use commit titles to create release notes. Your contribution will be featured
in a public release of the project. Think of it as a newspaper headline. It
should be descriptive and provide the reader a broad idea of what the commit is
about. You can use a related github issue if it matches this criterion. 

**Preferred title format**

`[#{issue_number}] {self_descriptive_title}`

Example

`[#258] - User can take the backup test successfully more than once`

optionally you can append the PR # between parenthesis.

#### Commit message's body

Use the body of the commit to bring more context to the change. Usually the bulk
of the problem might be explained in the GitHub Issue. It's a good long term strategy
not to rely on such elements. If the project were to change its hosting, much of the
associated "Issues" and "pull requests" will be lost, yet the commit history will
probably be preserved and the context will also be.

If there are followup issues for this commit, consider referencing those as well.

**Use the tools on your favor!**

When opening a Pull Request, GitHub will take the title of your commit as the PR's
title and the body of your PR its description. Having a proper structure on your
commit will make your day shorter.


### Example:

````
commit [some_hash]
Author: You <you@somedomain.io>
Date:   some date

    [#258] User can take the backup test successfully more than once (#282)
    
    Closes #258
    
    this checks that when the user taps the finished button on the phrase displayed it has definitely not passed the test before going to the recovery flow.
    
    Note: this should actually go to the next or previous screen according to the context that takes the user to the phrase display screen from that context.
    
    Add //TODO comment with the permanent fix for the problem
````

When you open a PR with a commit like this one the first line will land on the GUI's title field, 
and the body will be added as the description of the PR.

Adding the text `Closes #{issue_number}` will tell GitHub to close the issue when the PR is merged.

Let the machines do their work.
## Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

- (a) The contribution was created in whole or in part by me and I
      have the right to submit it under the open source license
      indicated in the file; or

- (b) The contribution is based upon previous work that, to the best
      of my knowledge, is covered under an appropriate open source
      license and I have the right under that license to submit that
      work with modifications, whether created in whole or in part
      by me, under the same open source license (unless I am
      permitted to submit under a different license), as indicated
      in the file; or

- (c) The contribution was provided directly to me by some other
      person who certified (a), (b) or (c) and I have not modified
      it.

- (d) I understand and agree that this project and the contribution
      are public and that a record of the contribution (including all
      personal information I submit with it, including my sign-off) is
      maintained indefinitely and may be redistributed consistent with
      this project or the open source license(s) involved.



This contribution guide is inspired on great projects like [AlamoFire](https://github.com/Alamofire/Foundation/blob/master/CONTRIBUTING.md) and [CocoaPods](https://github.com/CocoaPods/CocoaPods/blob/master/CONTRIBUTING.md)
