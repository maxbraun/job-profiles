Jenkins Job-Profiles
============

Basically we wan't to spend more time to develop great stuff instead of manage the whole day Jenkins jobs.
I wrote this Jenkins plugin which enables us to manage all our Jenkins jobs on at a specific point with the power of Jenkins plugins.

Requirements
------------
1. A list of all your software projects (=assets) you want to manage, with an ID, name, SCM-url, and optionally an category. [Example list](src/main/resources/softreg.xml).
2. Profiles, like in our [example repository](https://github.com/maxbraun/job-profiles-examles). Each profile contains one or more XML-files which representing the Jenkins Job Configuration.

To provide more projectspecific informations we're trying to read / parse projects like maven or composer. 

Concept
------------
Iterate throw the list of projects, find the right profile, parse the profile with the projects informations and send it to jenkins.

[![Build Status](https://travis-ci.org/maxbraun/job-profiles.png?branch=master)](https://travis-ci.org/maxbraun/job-profiles)
