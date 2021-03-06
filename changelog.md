### 0.1.22-SNAPSHOT
* Manually add interceptors to `ApiResourceControllerHandlerMapping`

### 0.1.22-SNAPSHOT
* Reworked `ApiResourceControllerHandlerMapping` to not be such a hack. Should play nice with JRebel / SpringLoaded

### 0.1.21-SNAPSHOT
* Bumped spring-type-hints to 0.1.2

### 0.1.20-SNAPSHOT
Skipped due to errors...

### 0.1.19-SNAPSHOT
* Updated spring-type-hints

### 0.1.18-SNAPSHOT
* Better Logging

### 0.1.17-SNAPSHOT
(fill me in)

### 0.1.16-SNAPSHOT
* Moved Assemblers behind an interface. `IApiResourceAssembler` will likely change soon
* Added spring-type-hints to help with resolutions. 
* Handled fringe cases with Proxies

### 0.1.15-SNAPSHOT
* Updated `spring-json-views` for `Page` serialization with view support

### 0.1.14-SNAPSHOT
* Added check to `ApiResourceSecurityAspect` in case repositories overload one of our repositories method names

### 0.1.13-SNAPSHOT
* Reworked `ApiResourceSecurityAdvisor`s to target repositories, simplifying the process.

### 0.1.12-SNAPSHOT
* Added `ApiResourceSecurityAdvisor` to advise synthesized controllers on security (via `ApiResourceSecurityAspect`)

### 0.1.11-SNAPSHOT
* Removed stupid security attempt
* Fixed dependencies

### 0.1.10-SNAPSHOT
* Renamed `@ApiResourceController` to `@ApiController` to prevent name collision
* Added support for `spring-json-view`
* Reworked `ApiResourceAssembler`'s `addLinks` so it delegates to a protected `addSelfLink` method if no default
controller is found.  

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
