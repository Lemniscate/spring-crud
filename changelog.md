

### 0.1.3-SNAPSHOT

* Swapped `ApiResourceServiceImpl`'s `delete` methods so that the one with the entity
performs the delete (whereas the one with the ID did it previously)
* Added `ApiResourceLifecycleListener` support