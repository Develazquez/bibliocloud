# Room Database + Hardware Features — BiblioCloud

Integrar Room como caché local de alto rendimiento con estrategia offline-first, y agregar 3 funcionalidades de hardware: captura de fotos de portadas (cámara), feedback háptico (vibración), y sensor de red (ConnectivityManager).

## Flujo de Operación de Room

**Escenarios cubiertos:**
1. **Caché de alto rendimiento** — Los datos del API se almacenan localmente en Room. El [Repository](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/di/RepositoryModule.kt#15-37) primero muestra datos cacheados (instantáneo) y luego actualiza desde la red.
2. **Consultas complejas y filtrado** — Búsqueda local por título/descripción y filtrado por categoría/estado se hacen directamente en SQL, sin llamar al servidor.
3. **Persistencia de estados** — El estado de cada préstamo (`ACTIVO`, `DEVUELTO`, `ATRASADO`) se persiste localmente, permitiendo consultar historial offline.

```
┌─────────────┐    ┌──────────────┐    ┌────────────┐    ┌───────────┐
│  ViewModel  │───►│  Repository  │───►│  Room DAO  │───►│  SQLite   │
│             │    │ (cache-first)│    │            │    │           │
│             │    │              │───►│  Retrofit  │───►│  API Go   │
└─────────────┘    └──────────────┘    └────────────┘    └───────────┘
```

---

## Proposed Changes

### Dependencias

#### [MODIFY] [libs.versions.toml](file:///c:/Users/govel/StudioProjects/bibliocloud/gradle/libs.versions.toml)
- Agregar versiones: `room = "2.6.1"`, `cameraX = "1.4.0"`
- Agregar librerías: `room-runtime`, `room-ktx`, `room-compiler`, `camerax-core`, `camerax-camera2`, `camerax-lifecycle`, `camerax-view`

#### [MODIFY] [build.gradle.kts](file:///c:/Users/govel/StudioProjects/bibliocloud/app/build.gradle.kts)
- Agregar dependencias de Room y CameraX

---

### Capa Data — Room (data/local)

#### [NEW] [RecursoEntity.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/entity/RecursoEntity.kt)
- Entidad Room mapeando campos de [Recurso](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/model/Recurso.kt#3-11) con `@Entity(tableName = "recursos")`
- PrimaryKey: [id](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/state/UiStates.kt#31-32) (String)

#### [NEW] [PrestamoEntity.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/entity/PrestamoEntity.kt)
- Entidad Room mapeando campos de [Prestamo](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/model/Prestamo.kt#5-16) con `@Entity(tableName = "prestamos")`
- PrimaryKey: [id](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/state/UiStates.kt#31-32) (String), ForeignKey: `recursoId` referencia a `recursos`

#### [NEW] [RecursoDao.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/dao/RecursoDao.kt)
- `@Insert(onConflict = REPLACE)` insertAll
- `@Query` getAll, getById, getByEstado, searchByTitulo (LIKE query), getByCategoria
- `@Query` deleteAll

#### [NEW] [PrestamoDao.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/dao/PrestamoDao.kt)
- `@Insert(onConflict = REPLACE)` insertAll
- `@Query` getByUsuarioId, getById, getByEstado (consulta compleja de filtrado)
- `@Query` deleteAll

#### [NEW] [BiblioCloudDatabase.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/BiblioCloudDatabase.kt)
- `@Database(entities = [RecursoEntity, PrestamoEntity], version = 1)`
- Abstract DAOs

#### [NEW] [EntityMappers.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/entity/EntityMappers.kt)
- `RecursoEntity.toDomain() → Recurso`, `Recurso.toEntity() → RecursoEntity`
- `PrestamoEntity.toDomain() → Prestamo`, `Prestamo.toEntity() → PrestamoEntity`

---

### Capa Data — Conectividad

#### [NEW] [NetworkMonitor.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/NetworkMonitor.kt)
- Usa `ConnectivityManager` y `NetworkCallback` para exponer `StateFlow<Boolean>` con el estado de red actual
- **Hardware #3: Sensor de red** — detecta cambios de conectividad en tiempo real

---

### Capa DI

#### [MODIFY] [LocalModule.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/di/LocalModule.kt)
- Proveer `BiblioCloudDatabase` con `Room.databaseBuilder()`
- Proveer `RecursoDao` y `PrestamoDao` desde la database
- Proveer `NetworkMonitor` como Singleton

---

### Capa Data — Repositorios (cache-first)

#### [MODIFY] [RecursoRepositoryImpl.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/repository/RecursoRepositoryImpl.kt)
- Inyectar `RecursoDao` y `NetworkMonitor`
- **[getRecursos()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/repository/RecursoRepository.kt#7-8)**: Leer caché → si hay red, fetch API → guardar en caché → retornar datos frescos
- **[buscarRecursos()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/repository/RecursoRepositoryImpl.kt#68-86)**: Búsqueda SQL local con `LIKE` (consulta compleja local)
- **[getRecursosPorCategoria()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/repository/RecursoRepository.kt#11-12)**: Filtrado SQL local por categoría

#### [MODIFY] [PrestamoRepositoryImpl.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/repository/PrestamoRepositoryImpl.kt)
- Inyectar `PrestamoDao` y `NetworkMonitor`
- **[getMisPrestamos()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/repository/PrestamoRepositoryImpl.kt#82-104)**: Caché-first con actualización desde API
- **[solicitarPrestamo()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/repository/PrestamoRepository.kt#6-7)**: Si hay red, solicita y guarda resultado en Room
- **[devolverPrestamo()](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/domain/repository/PrestamoRepository.kt#7-8)**: Si hay red, devuelve y actualiza caché

---

### Hardware Features

#### [NEW] [CapturePhotoScreen.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/view/CapturePhotoScreen.kt)
- **Hardware #1: Cámara** — Pantalla con vista previa de CameraX para fotografiar portadas de libros
- Captura la imagen, la guarda en almacenamiento local y retorna la URI al recurso para que el API la suba a Cloudinary

#### [NEW] [CapturePhotoViewModel.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/viewmodel/CapturePhotoViewModel.kt)
- Gestiona el flujo de captura, guarda la foto y expone la URI resultante

#### [NEW] [HardwareUtils.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/data/local/HardwareUtils.kt)
- **Hardware #2: Vibración** — Función `vibrateOnAction(context)` que usa `Vibrator`/`VibratorManager`
- Soporta API 26+ con `VibrationEffect`

#### [MODIFY] [RecursosDetailScreen.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/view/RecursosDetailScreen.kt)
- Llamar a `vibrateOnAction()` al confirmar préstamo exitosamente

#### [MODIFY] [MyLoanScreen.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/view/MyLoanScreen.kt)
- Llamar a `vibrateOnAction()` al devolver préstamo exitosamente

#### [MODIFY] [CatalogScreen.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/view/CatalogScreen.kt)
- Agregar botón de capturar foto de portada en la TopAppBar
- Mostrar banner de "Sin conexión — mostrando datos locales" cuando no hay red

#### [MODIFY] [Navigation.kt](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/java/com/develazquez/bibliocloud/presentation/view/Navigation.kt)
- Agregar ruta `Screen.CapturePhoto`

#### [MODIFY] [AndroidManifest.xml](file:///c:/Users/govel/StudioProjects/bibliocloud/app/src/main/AndroidManifest.xml)
- Agregar `<uses-permission android:name="android.permission.CAMERA" />`
- Agregar `<uses-permission android:name="android.permission.VIBRATE" />`

---

## Verification Plan

### Build Verification
```
cd c:\Users\govel\StudioProjects\bibliocloud
.\gradlew.bat assembleDebug
```
El proyecto debe compilar sin errores.

### Manual Verification
1. **Caché Room**: Abrir la app con conexión → navegar al catálogo → cerrar la app → activar modo avión → reabrir la app → los recursos deben aparecer cargados desde caché local.
2. **Búsqueda local**: Con el catálogo cargado, usar búsqueda por texto — debe funcionar sin red.
3. **Captura de foto**: En el catálogo, presionar el botón de cámara → permitir permiso → tomar foto de portada → la imagen se guarda localmente y se obtiene la URI.
4. **Vibración**: Solicitar un préstamo → al confirmar exitosamente, el teléfono debe vibrar brevemente.
5. **Sensor de red**: Desactivar WiFi/datos → debe aparecer un banner indicando "Sin conexión" en el catálogo.
