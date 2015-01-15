### 0.1.9-SNAPSHOT
* Added `ApiResourceControllerSupport` with `ApiResourceServices`, `ApiResourceAssemblers`, and `conversionService`

### 0.1.8-SNAPSHOT
* Added `ApiResourceServices` to help dynamically access the framework methods in a typesafe manner.  

### 0.1.7-SNAPSHOT
* Added `delete(Iterable)` and `findByIds(Iterable)` methods 

### 0.1.6-SNAPSHOT
* Added `ApiResourceAssemblers` bean to easily marshall objects to responses
* Added first iteration of `ApiResourceRegistry` bean to easily lookup service-objects (though it only has Assemblers so far)

### 0.1.5-SNAPSHOT
* Updated to Spring-Search 0.1.5 to fix Between bug
* Added timing metrics to processor (#8)

### 0.1.4-SNAPSHOT
* Bumped javassist-util to 0.1.2 for missing class bugfix
* Added `@ApiResourceController` to be used instead of `@Controller`
* Adjusted `ApiResourceService`'s `update` & `updateForRead` to take IDs for finding the entity
* Bugfix on  `ApiResourceServiceImpl`'s `update` method to load the entity & update it (rather than creating a new one)

### 0.1.3-SNAPSHOT

* Swapped `ApiResourceServiceImpl`'s `delete` methods so that the one with the entity
performs the delete (whereas the one with the ID did it previously)
* Added `ApiResourceLifecycleListener` support
