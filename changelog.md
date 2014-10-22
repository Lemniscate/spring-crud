### 0.1.4-SNAPSHOT
* Bumped javassist-util to 0.1.2 for missing class bugfix
* Added `@ApiResourceController` to be used instead of `@Controller`
* Adjusted `ApiResourceService`'s `update` & `updateForRead` to take IDs for finding the entity
* Bugfix on  `ApiResourceServiceImpl`'s `update` method to load the entity & update it (rather than creating a new one)

### 0.1.3-SNAPSHOT

* Swapped `ApiResourceServiceImpl`'s `delete` methods so that the one with the entity
performs the delete (whereas the one with the ID did it previously)
* Added `ApiResourceLifecycleListener` support
