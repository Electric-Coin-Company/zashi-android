# Overall:

- Does the contribution reference an existing GitHub issue?
- Are the requirements well defined?
- Does static analysis pass?  (e.g. `./gradlew detektAll`, `./gradlew lint`, `./gradlew ktlint`)

#  Specification:

- Are the requirements for the change well specified? Where are they documented? Bugfixes need a clear specification of the bug's cause and fix. (Small PRs might specify in a GitHub issue. Large changes may require stand-alone docs.)
- Is there a documented test plan, which describes how to manually verify the change works on testnet prior to a release?

# User Facing Documentation:

- What is the "changelog" entry for this change? (All changes should include this.)
- Are there any changes which require updates to user facing documentation?  If so, does the new documentation make sense? (documentation should be placed in [.](/docs) folder of the repo)

# Testing:

- For non-minor PRs (up to the code reviewers and PR creator if this is needed), we require authors to perform a thorough self-review and self-test of the resulting code base, including use cases that might not be visible affected by the introduced changes. Reviewers are not expected to run the changes locally, but are definitely encouraged to do so at their best judgement.
- When introducing modifications that affect the UI, screenshots might be provided in a BEFORE/AFTER fashion to speed up UI/UX requirement validation.
- Are there new tests that check all of the requirements? If it's a bugfix does it include new tests testing the bug triggering condition? (Do they fail before the fix and pass after the fix?)
- Do tests include edge cases, error conditions, and "negative case" tests to ensure the software is robust? Example: a function for verifying transaction signatures should include a bunch of tests for invalid signature cases.
- Do tests include all "logical test coverage" in addition to requirements-focused tests? Logical test coverage verifies behavior of the code that isn't obvious or implied by the requirements themselves.
- Have all of the automated tests been executed by CI? Read over the CI report to verify this. (Note: our CI system may not currently provide test results prior to review. We should change this! In the meantime, the reviewer should run the tests.)
- Is it clear that the test plan does actually test the new change? Does the test plan include any edge case / negative case / error conditions?

# Code Review:
- Does the code structure make sense? Does it follow existing conventions / frameworks of existing code, or is it introducing new abstractions / structure?
- Is the code style consistent with standard guidelines?
    - [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html)
    - [Android Open Source Project Conventions](https://source.android.com/source/code-style.html)
- Does every change make sense? Make sure to ask questions, even or especially "basic" coding questions ("what does this mean in Kotlin?") and domain-newbie questions ("what is the priority system, and why does this line change the priority?").

In summary, here's a checklist that summarizes what reviewers should be looking for when doing a Code Review [1]

- The code is well-designed.
- The functionality is good for the users of the code.
- Any UI changes are sensible and look good.
- Any parallel programming is done safely.
- The code isn’t more complex than it needs to be.
- The developer isn’t implementing things they might need in the future but don’t know they need now or aren't using now.
- Code has appropriate unit tests.
- Tests are well-designed.
- The developer used clear names for everything.
- Comments are clear and useful, and mostly explain why instead of what.
- Code is appropriately documented.
- The code conforms to our style guides.
- Make sure to review every line of code you’ve been asked to review, look at the context, make sure you’re improving code health, and compliment developers on good things that they do.

[[1] What to look for in a code review](https://google.github.io/eng-practices/review/reviewer/looking-for.html)