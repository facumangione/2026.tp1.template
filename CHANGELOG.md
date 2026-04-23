# Changelog - BiblioTech

## [Unreleased]

## [Sprint 4] - 2026-04-21
### Added
- Interfaz genérica `Repository<T, ID>`
- `LibroRepositoryImpl` con almacenamiento en HashMap
- `SocioRepositoryImpl` con almacenamiento en HashMap

## [Sprint 3] - 2026-04-21
### Added
- `BibliotecaException` como clase base de todos los errores de negocio
- `LibroNoDisponibleException`
- `LibroNoEncontradoException`
- `SocioNoEncontradoException`
- `LimitePrestamosException`

## [Sprint 2] - 2026-04-20
### Added
- Enum `CategoriaSocio` con valores ESTUDIANTE y DOCENTE
- Clase abstracta `Socio` con lógica común de préstamos
- `SocioEstudiante` con límite de 3 préstamos
- `SocioDocente` con límite de 5 préstamos

## [Sprint 1] - 2026-04-19
### Added
- Interfaz `Recurso` como contrato base para recursos prestables
- Record `Libro` implementando `Recurso`
- Record `Ebook` implementando `Recurso`
- Enum `CategoriaSocio` con valores ESTUDIANTE y DOCENTE