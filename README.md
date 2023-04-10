# DST

My solution for DST(Distributed System Technologies) Spring Semester 2023 at [TU Wien](https://www.tuwien.at/en/).

## Theory

My answers to the theory questions are written down in theory.md, which I
compiled to a PDF with the following command:

```bash
pandoc --from gfm \
       --variable mainfont="Inter"  \
       --variable monofont="JetBrains Mono" \
       --pdf-engine=lualatex \
       -o theory.pdf \
       theory.md
```

## Insomnia Config

In some examples you need to test the REST API and and I did that with [Insomnia](https://insomnia.rest/) (instead of
Postman).
You can import my config(v4) from `insomnia.yaml`.

## Build Java

Each Maven module has a corresponding profile that contains the build config for the respective module.
For example, to build the JPA task of assignment 1, run

    mvn clean install -Pass1-jpa

You can add the `-DskipTests` flag to skip JUnit test execution.

There is also a profile `all`, that includes all modules.
You should activate this profile in your IDE's Maven ([IDEA],[Eclipse]) configuration!

[IDEA]: https://www.jetbrains.com/help/idea/maven-support.html

[Eclipse]: http://www.eclipse.org/m2e/documentation/release-notes-15.html#new-maven-profile-management-ui
